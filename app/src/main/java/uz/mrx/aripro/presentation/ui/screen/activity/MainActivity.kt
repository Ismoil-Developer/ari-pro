package uz.mrx.aripro.presentation.ui.screen.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.presentation.navigation.NavigationHandler
import uz.mrx.aripro.presentation.ui.dialog.OrderTimeDialog
import uz.mrx.aripro.presentation.ui.viewmodel.main.MainViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.main.impl.MainViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var handler: NavigationHandler

    @Inject
    lateinit var sharedPreference: MySharedPreference

    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.connectWebSocket("ws://ari.digitallaboratory.uz/ws/pro/connect/", sharedPreference.token)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        observeIncomingOrders()

        handler.navigationStack.onEach {
            it.invoke(navController)
        }.launchIn(lifecycleScope)
    }

    private fun observeIncomingOrders() {
        lifecycleScope.launch {
            viewModel.incomingOrders.collectLatest { newOrder ->
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
                val bundle = Bundle().apply {
                    putInt("orderId", newOrder.orderId)
                }

                navController.navigate(R.id.orderDeliveryScreen, bundle)

            },
            onSkipClickListener = {
                viewModel.rejectOrder(newOrder.orderId)
                Toast.makeText(this, "Zakaz rad etildi", Toast.LENGTH_SHORT).show()
            }
        )

        orderTimeDialog.show()

    }
}
