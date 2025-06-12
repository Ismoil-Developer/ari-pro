package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.databinding.ScreenOrderDeliveryBinding
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderDeliveryScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderDeliveryScreenViewModelImpl

@AndroidEntryPoint
class OrderDeliveryScreen : Fragment(R.layout.screen_order_delivery) {

    private val binding: ScreenOrderDeliveryBinding by viewBinding(ScreenOrderDeliveryBinding::bind)
    private val viewModel: OrderDeliveryScreenViewModel by viewModels<OrderDeliveryScreenViewModelImpl>()
    private val args:OrderDeliveryScreenArgs by navArgs()

    private lateinit var mapView: MapView
    private var userLocationLayer: UserLocationLayer? = null

    private lateinit var drivingRouter: DrivingRouter


    private var phone: String? = null
    private var orderId: Int? = null
    private var currentDirection: String? = null

    private val directions = listOf(
        "en_route_to_store",
        "arrived_at_store",
        "picked_up",
        "en_route_to_customer",
        "arrived_to_customer")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView

        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow).apply {
            isVisible = true
            isHeadingEnabled = true
        }


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        binding.message.setOnClickListener {
            viewModel.openChatScreen()
        }


        binding.orderCancel.setOnClickListener {
            if (orderId != -1){
                viewModel.orderCancelScreen(orderId!!)
            }
        }

        binding.detail.setOnClickListener {
            if (orderId != -1){
                viewModel.openOrderDetailScreen(orderId!!)
            }
        }

        binding.gps.setOnClickListener {
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
            }
            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val settingsClient =
                com.google.android.gms.location.LocationServices.getSettingsClient(requireActivity())
            val task = settingsClient.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // GPS yoqilgan, joylashuvni olish
                getLastKnownLocation { location ->
                    if (location != null) {

                        val userLocation = Point(location.latitude, location.longitude)
                        mapView.map.move(
                            CameraPosition(userLocation, 15.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Joylashuvni olish uchun ruhsat bering",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


        if (args.id != -1){
            viewModel.getOrderActive(args.id)
        }else{
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orderActiveToken.collectLatest { order ->



                    orderId = order.id
                    currentDirection = order.direction

                    phone = order.customer_info.phone_number
                    binding.courierRating.text = order.customer_info.rating.toString()

                    if (!order.customer_info.avatar.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(order.customer_info.avatar).into(binding.prf)
                    }

                    binding.courierName.text = order.customer_info.full_name
                    binding.swipeView.setText(getButtonText(order.direction))

                    // step indikatorni yangilash

                }
            }
        }

        binding.btnContinue.setOnClickListener {
            val defaultLat = 41.311081
            val defaultLong = 69.240562
            val uri = Uri.parse("geo:$defaultLat,$defaultLong?q=$defaultLat,$defaultLong(Yetkazib+berish+joyi)")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val chooser = Intent.createChooser(intent, "Xaritani tanlang")
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooser)
            }
        }

        binding.swipeView.setOnSwipeListener {
            val current = currentDirection ?: return@setOnSwipeListener
            val next = getNextDirection(current)
            if (next != null) {
                orderId?.let { id ->
                    viewModel.postDirection(id, DirectionRequest(next))
                }
            } else {
                Toast.makeText(requireContext(), "Buyurtma yakunlandi", Toast.LENGTH_SHORT).show()
                viewModel.openOrderCompletedScreen(orderId!!)
            }
        }

        binding.call.setOnClickListener {
            phone?.let { number ->
                if (number.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$number")
                    }
                    startActivity(intent)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.responseDirection.collectLatest {
                currentDirection = it.direction
                binding.swipeView.setText(getButtonText(it.direction))
                binding.swipeView.reset()

                updateDeliverySteps(it.direction)

            }
        }

        if (args.id != -1){
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orderActiveResponse.collectLatest { order ->
                    orderId = order.id
                    currentDirection = order.direction

                    phone = order.customer_info.phone_number
                    binding.courierRating.text = order.customer_info.rating.toString()

                    if (!order.customer_info.avatar.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(order.customer_info.avatar).into(binding.prf)
                    }

                    binding.courierName.text = order.customer_info.full_name
                    binding.swipeView.setText(getButtonText(order.direction))

                    // step indikatorni yangilash

                }
            }
        }


    }


    private fun getLastKnownLocation(callback: (android.location.Location?) -> Unit) {
        val fusedLocationClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
                interval = 1000 // 1 sekund
                fastestInterval = 500
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    callback(location)
                } else {
                    // Agar oxirgi joylashuv yo'q bo'lsa, real vaqtda joylashuvni oling
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                val currentLocation = locationResult.lastLocation
                                callback(currentLocation)
                                fusedLocationClient.removeLocationUpdates(this) // Faqat bir marta joylashuvni oling
                            }
                        },
                        null
                    )
                }
            }.addOnFailureListener { exception ->
                Log.e("LocationError", "Joylashuvni olishda xatolik: ${exception.message}")
                callback(null)
            }
        } else {
            // Ruxsat so'rash
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            callback(null)
        }
    }



    private fun getNextDirection(currentDirection: String): String? {
        val index = directions.indexOf(currentDirection)
        return if (index in 0 until directions.lastIndex) directions[index + 1] else null
    }

    private fun getButtonText(direction: String): String {
        val directions = listOf(
            "en_route_to_store",
            "arrived_at_store",
            "picked_up",
            "en_route_to_customer",
            "arrived_to_customer")

        val nextIndex = directions.indexOf(direction) + 1
        val nextDirection = directions.getOrNull(nextIndex)

        return when (nextDirection) {
            "arrived_at_store" -> "Do‘konga yetib keldi"
            "picked_up" -> "Yukni oldi"
            "en_route_to_customer" -> "Mijozga yo‘lda"
            "arrived_to_customer" -> "Manziliga yetib keldi"
            else -> "Buyurtma yakunlandi"
        }
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

}