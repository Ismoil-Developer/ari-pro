package uz.mrx.aripro.presentation.ui.screen.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.presentation.navigation.NavigationHandler
import uz.mrx.aripro.presentation.ui.dialog.OrderTimeDialog
import uz.mrx.aripro.presentation.ui.viewmodel.main.MainViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.main.impl.MainViewModelImpl
import uz.mrx.aripro.utils.CourierLocationService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var handler: NavigationHandler

    @Inject
    lateinit var sharedPreference: MySharedPreference

    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private lateinit var navController: NavController

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
    private val settingsClient by lazy { LocationServices.getSettingsClient(this) }



    @Inject
    lateinit var webSocketClient: CourierWebSocketClient

    private val gpsEnableLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // ✅ GPS yoqildi, endi location yuborishni boshlaymiz
            webSocketClient.startSendingLocationUpdates(this)
        } else {
            Log.e("MainActivity", "❌ Foydalanuvchi GPSni yoqmadi")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }


        val serviceIntent = Intent(this, CourierLocationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }


        if (sharedPreference.token.isNotEmpty()){
            webSocketClient.connect(
                url = "ws://ari.digitallaboratory.uz/ws/pro/connect/",
                token = sharedPreference.token
            )

            viewModel.connectWebSocket(url = "ws://ari.digitallaboratory.uz/ws/pro/connect/",
                token = sharedPreference.token)

        }

        lifecycleScope.launch {
            webSocketClient.orderTakens.collectLatest { newOrder ->

                val bundle = Bundle().apply {
                    putInt("orderId", newOrder.orderId)
                }

                navController.navigate(R.id.orderDeliveryScreen, bundle)
            }
        }


        checkAndPromptEnableGPS {
            webSocketClient.startSendingLocationUpdates(this)
        }

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        observeIncomingOrders()

        handler.navigationStack.onEach {
            it.invoke(navController)
        }.launchIn(lifecycleScope)

    }

    private fun checkAndPromptEnableGPS(onGpsEnabled: () -> Unit) {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                onGpsEnabled() // ✅ GPS allaqachon yoqilgan
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(
                            exception.resolution.intentSender
                        ).build()
                        gpsEnableLauncher.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "❌ GPS dialog ochilmadi: ${e.localizedMessage}")
                    }
                }
            }

    }

    private fun observeIncomingOrders() {
        lifecycleScope.launch {
            webSocketClient.incomingOrders.collectLatest { newOrder ->
                Log.d("DDDDDDDDD", "observeIncomingOrders: ${newOrder.orderItems}")
                showNewOrderDialog(newOrder)
            }
        }
    }

    private fun showNewOrderDialog(newOrder: WebSocketOrderEvent.NewOrder) {
        val orderDesc = newOrder.orderItems
        val orderPrice = "${newOrder.price} so'm"

        val orderTimeDialog = OrderTimeDialog(
            this,
            orderDesc,
            orderPrice,
            onAcceptClickListener = {
                viewModel.acceptOrder(newOrder.orderId)
                Toast.makeText(this, "Zakaz qabul qilindi", Toast.LENGTH_SHORT).show()

                // ✅ Zakaz qabul qilingach, OrderDeliveryScreen fragmentiga o'tish


            },
            onSkipClickListener = {
                viewModel.rejectOrder(newOrder.orderId)
                Toast.makeText(this, "Zakaz rad etildi", Toast.LENGTH_SHORT).show()
            }
        )

        orderTimeDialog.show()

    }

    override fun onResume() {
        super.onResume()
        if (!isGpsEnabled()) {
            checkAndPromptEnableGPS {
                webSocketClient.startSendingLocationUpdates(this)
            }
        } else {
            webSocketClient.startSendingLocationUpdates(this)
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }

}