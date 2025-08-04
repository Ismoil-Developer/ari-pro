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
import uz.mrx.aripro.presentation.ui.dialog.ProgressDialogFragment
import uz.mrx.aripro.presentation.ui.viewmodel.main.MainViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.main.MainViewModelImpl
import uz.mrx.aripro.utils.CourierLocationService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // DI bilan inject qilinadigan komponentlar
    @Inject
    lateinit var handler: NavigationHandler
    @Inject
    lateinit var mySharedPreference: MySharedPreference

    private var progressDialogFragment: ProgressDialogFragment? = null


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val allGranted = permissions.all { it.value }

        if (allGranted) {
            startCourierLocationServiceIfNeeded()
        } else {

            Toast.makeText(this, "Permissionlar berilmadi!", Toast.LENGTH_SHORT).show()

        }
    }

    @Inject
    lateinit var webSocketClient: CourierWebSocketClient

    // ViewModel
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()

    // Navigation
    private lateinit var navController: NavController

    // GPS uchun kerakli so'rov
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L
    ).build()

    private val settingsClient by lazy {
        LocationServices.getSettingsClient(this)
    }

    // GPS yoqilishini so'raydigan launcher
    private val gpsEnableLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                webSocketClient.startSendingLocationUpdates(this)
            } else {
                Log.e("MainActivity", "❌ Foydalanuvchi GPSni yoqmadi")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // WebSocket ulanishi
        if (mySharedPreference.token.isNotEmpty()) {
            val wsUrl = "ws://ari-delivery.uz/ws/pro/connect/"
            webSocketClient.connect(url = wsUrl, token = mySharedPreference.token)
            viewModel.connectWebSocket(url = wsUrl, token = mySharedPreference.token)
        }

        // Buyurtmalarni qabul qilish
        lifecycleScope.launch {
            webSocketClient.orderAssigned.collectLatest { newOrder ->

                progressDialogFragment?.dismiss()
                progressDialogFragment = null

                val bundle = Bundle().apply { putInt("orderId", newOrder.orderId) }
                navController.navigate(R.id.orderDeliveryScreen, bundle)

            }
        }

        // GPS yoqilganini tekshirish
        checkAndPromptEnableGPS {
            webSocketClient.startSendingLocationUpdates(this)
        }

        // Status barni shaffof qilish
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

        // Navigation Controller setup
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        // Yangi buyurtmalarni kuzatish
        observeIncomingOrders()

        // Navigation stack kuzatuvi
        handler.navigationStack.onEach {
            it.invoke(navController)
        }.launchIn(lifecycleScope)

    }

    private fun observeIncomingOrders() {
        lifecycleScope.launch {
            webSocketClient.incomingOrders.collectLatest { newOrder ->
                Log.d("NewOrder", "orderItems: ${newOrder.orderItems}")
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

                progressDialogFragment = ProgressDialogFragment(100) {

                }

                progressDialogFragment?.show(supportFragmentManager, "progressDialog")

                Toast.makeText(this, "Zakaz qabul qilindi", Toast.LENGTH_SHORT).show()

            },
            onSkipClickListener = {
                viewModel.rejectOrder(newOrder.orderId)
                Toast.makeText(this, "Zakaz rad etildi", Toast.LENGTH_SHORT).show()
            }
        )

        orderTimeDialog.show()

    }

    private fun checkAndPromptEnableGPS(onGpsEnabled: () -> Unit) {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener { onGpsEnabled() }
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

    private fun checkPermissionsAndStartService() {
        if (mySharedPreference.token.isNotEmpty()) {
            val permissionsNeeded = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            // Android 14 dan boshlab yangi permission ham so‘raladi
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
            }

            val notGranted = permissionsNeeded.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGranted.isEmpty()) {
                startCourierLocationServiceIfNeeded()
            } else {
                locationPermissionRequest.launch(notGranted.toTypedArray())
            }
        }
    }

    private fun startCourierLocationServiceIfNeeded() {
        val serviceIntent = Intent(this, CourierLocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        } else {
            checkPermissionsAndStartService()
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        } else {
            checkPermissionsAndStartService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionsAndStartService()
            } else {
                Toast.makeText(this, "📢 Notification permission bermadingiz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}