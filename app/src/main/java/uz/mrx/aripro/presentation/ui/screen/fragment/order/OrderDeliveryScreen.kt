package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import uz.mrx.aripro.databinding.ScreenOrderDeliveryBinding
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderDeliveryScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderDeliveryScreenViewModelImpl
import javax.inject.Inject


@AndroidEntryPoint
class OrderDeliveryScreen : Fragment(R.layout.screen_order_delivery) {

    private val binding: ScreenOrderDeliveryBinding by viewBinding(ScreenOrderDeliveryBinding::bind)
    private val viewModel: OrderDeliveryScreenViewModel by viewModels<OrderDeliveryScreenViewModelImpl>()
    private val args: OrderDeliveryScreenArgs by navArgs()

    @Inject
    lateinit var sharedPref: MySharedPreference

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            moveToCurrentLocation()
            courierWebSocketClient.startSendingLocationUpdates(requireContext())
        } else {
            Toast.makeText(requireContext(), "Joylashuv ruxsat berilmadi", Toast.LENGTH_SHORT).show()
        }
    }

    @Inject
    lateinit var courierWebSocketClient:CourierWebSocketClient


    private var phone: String? = null
    private var orderId: Int? = null
    private var currentDirection: String? = null

    private val directions = listOf(
        "en_route_to_store",
        "arrived_at_store",
        "picked_up",
        "en_route_to_customer",
        "arrived_to_customer"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courierWebSocketClient.connect("ws://ari.uzfati.uz/ws/pro/connect/", sharedPref.token)

        checkAndRequestLocationPermission()

        binding.gps.setOnClickListener { moveToCurrentLocation() }

        binding.message.setOnClickListener { viewModel.openChatScreen() }
        binding.call.setOnClickListener { phone?.let { dialNumber(it) } }
        binding.detail.setOnClickListener { orderId?.let { viewModel.openOrderDetailScreen(it) } }
        binding.orderCancel.setOnClickListener { orderId?.let { viewModel.orderCancelScreen(it) } }

        binding.btnContinue.setOnClickListener {
            val lat = 41.311081
            val long = 69.240562
            val uri = Uri.parse("geo:$lat,$long?q=$lat,$long(Yetkazib+berish+joyi)")
            startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, uri), "Xaritani tanlang"))
        }

        binding.swipeView.setOnSwipeListener {
            val next = getNextDirection(currentDirection ?: return@setOnSwipeListener)
            if (next != null) {
                orderId?.let { viewModel.postDirection(it, DirectionRequest(next)) }
            } else {
                Toast.makeText(requireContext(), "Buyurtma yakunlandi", Toast.LENGTH_SHORT).show()
                viewModel.openOrderCompletedScreen(orderId!!)
            }
        }

        observeOrderState()
        observeDirectionUpdates()
        observeErrors()

    }

    private fun checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            moveToCurrentLocation()
            courierWebSocketClient.startSendingLocationUpdates(requireContext())
        }
    }


    @SuppressLint("MissingPermission")
    private fun moveToCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val point = Point(it.latitude, it.longitude)
                binding.mapView.map.move(
                    CameraPosition(point, 15.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            } ?: Toast.makeText(requireContext(), "Joylashuv topilmadi", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendCurrentLocation() {
        lifecycleScope.launch {
            try {
                val location = LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.await()
                location?.let {
//                    viewModel.startSendingLocation { Pair(it.latitude, it.longitude) }
                }
            } catch (e: Exception) {
                Log.e("Location", "Error: ${e.message}")
            }
        }
    }

    private fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    private fun observeOrderState() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (args.id != -1) {
                viewModel.getOrderActive(args.id)
                viewModel.orderActiveResponse.collectLatest { updateOrderUI(it) }
            } else {
                viewModel.orderActiveToken.collectLatest { it?.let { updateOrderUI(it) } }
            }
        }
    }

    private fun updateOrderUI(order: OrderActiveResponse) {
        orderId = order.id
        currentDirection = order.direction
        phone = order.customer_info.phone_number

        binding.courierName.text = order.customer_info.full_name
        binding.courierRating.text = order.customer_info.rating.toString()
        binding.swipeView.setText(getButtonText(order.direction))

        Glide.with(requireContext())
            .load(order.customer_info.avatar)
            .into(binding.prf)
    }

    private fun observeDirectionUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.responseDirection.collectLatest {
                currentDirection = it.direction
                binding.swipeView.setText(getButtonText(it.direction))
                binding.swipeView.reset()
                updateDeliverySteps(it.direction)
            }
        }
    }

    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeErrorResponse.collectLatest {
                val message = if (it.contains("No active order")) {
                    "Sizda hozirda faol zakaz mavjud emas"
                } else {
                    "Xatolik yuz berdi: $it"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getButtonText(direction: String): String {
        return when (direction) {
            "en_route_to_store" -> "Do'konga yo'l olindi"
            "arrived_at_store" -> "Do'konga yetib kelindi"
            "picked_up" -> "Buyurtma olindi"
            "en_route_to_customer" -> "Mijoz tomon yo'lga chiqildi"
            "arrived_to_customer" -> "Mijozga yetib kelindi"
            else -> "Boshlash"
        }
    }

    private fun getNextDirection(current: String): String? {
        val index = directions.indexOf(current)
        return if (index != -1 && index < directions.size - 1) directions[index + 1] else null
    }

    private fun updateDeliverySteps(status: String) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.buttonBgColor)
        val inactiveColor = Color.parseColor("#DDDDDD")

        // Barchasini default: inactive
        binding.arrivedAtStore.setColorFilter(inactiveColor)
        binding.pickedUp.setColorFilter(inactiveColor)
        binding.enRouteToCustomer.setColorFilter(inactiveColor)
        binding.handedOver.setColorFilter(inactiveColor)

        binding.line.setBackgroundColor(inactiveColor)
        binding.line2.setBackgroundColor(inactiveColor)
        binding.arrivedToCustomer.setBackgroundColor(inactiveColor)

        // Holatga qarab aktivlashtiramiz
        when (status) {
            "arrived_at_store" -> {
                binding.arrivedAtStore.setColorFilter(activeColor)
            }

            "picked_up" -> {
                binding.arrivedAtStore.setColorFilter(activeColor)
                binding.line.setBackgroundColor(activeColor)
                binding.pickedUp.setColorFilter(activeColor)
            }

            "en_route_to_customer" -> {
                binding.arrivedAtStore.setColorFilter(activeColor)
                binding.line.setBackgroundColor(activeColor)
                binding.pickedUp.setColorFilter(activeColor)
                binding.line2.setBackgroundColor(activeColor)
                binding.enRouteToCustomer.setColorFilter(activeColor)
            }

            "arrived_to_customer" -> {
                binding.arrivedAtStore.setColorFilter(activeColor)
                binding.line.setBackgroundColor(activeColor)
                binding.pickedUp.setColorFilter(activeColor)
                binding.line2.setBackgroundColor(activeColor)
                binding.enRouteToCustomer.setColorFilter(activeColor)
                binding.arrivedToCustomer.setBackgroundColor(activeColor)
                binding.handedOver.setColorFilter(activeColor)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            sendCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Joylashuv ruxsati talab qilinadi", Toast.LENGTH_SHORT).show()
        }
    }

}





//    override fun onStart() {
//        super.onStart()
//        MapKitFactory.getInstance().onStart()
//        binding.mapView.onStart()
//    }
//
//    override fun onStop() {
//        binding.mapView.onStop()
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }





