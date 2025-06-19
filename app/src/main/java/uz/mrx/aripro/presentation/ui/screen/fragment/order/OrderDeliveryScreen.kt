package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
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


        viewModel.connectWebSocket("ws://ari.uzfati.uz/ws/pro/connect/", sharedPref.token)

        val mapView = binding.mapView
        val mapKit = MapKitFactory.getInstance()



        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Ruxsat so'rash
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startSendingLocation()
        }



        binding.message.setOnClickListener {
            viewModel.openChatScreen()
        }


        binding.orderCancel.setOnClickListener {

            if (orderId != null && orderId != -1) {
                viewModel.orderCancelScreen(orderId!!)
            }

        }

        binding.detail.setOnClickListener {
            if (orderId != null && orderId != -1) {
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


        if (args.id != -1) {
            viewModel.getOrderActive(args.id)
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orderActiveToken.collectLatest { order ->
                    if (order != null) {
                        orderId = order.id
                        currentDirection = order.direction

                        phone = order.customer_info.phone_number
                        binding.courierRating.text = order.customer_info.rating.toString()

                        if (!order.customer_info.avatar.isNullOrEmpty()) {
                            Glide.with(requireContext()).load(order.customer_info.avatar)
                                .into(binding.prf)
                        }

                        binding.courierName.text = order.customer_info.full_name
                        binding.swipeView.setText(getButtonText(order.direction))
                    } else {
                        // optional: error UI
                    }
                }
            }

        }

        binding.btnContinue.setOnClickListener {
            val defaultLat = 41.311081
            val defaultLong = 69.240562
            val uri =
                Uri.parse("geo:$defaultLat,$defaultLong?q=$defaultLat,$defaultLong(Yetkazib+berish+joyi)")
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeErrorResponse.collectLatest { errorMessage ->
                // Bu yerda 404 xatoni qayta ishlash
                if (errorMessage.contains("No active order")) {
                    Toast.makeText(
                        requireContext(),
                        "Sizda hozirda faol zakaz mavjud emas",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Istasangiz boshqa sahifaga yo'naltiring yoki ekranda xabar chiqaring
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Xatolik yuz berdi: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        if (args.id != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orderActiveResponse.collectLatest { order ->
                    orderId = order.id
                    currentDirection = order.direction

                    phone = order.customer_info.phone_number
                    binding.courierRating.text = order.customer_info.rating.toString()

                    if (!order.customer_info.avatar.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(order.customer_info.avatar)
                            .into(binding.prf)
                    }

                    binding.courierName.text = order.customer_info.full_name
                    binding.swipeView.setText(getButtonText(order.direction))

                    // step indikatorni yangilash

                }
            }
        }


    }


    private fun startSendingLocation() {
        lifecycleScope.launch {
            try {
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireContext())
                val location = fusedLocationClient.lastLocation.await()

                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude

                    viewModel.startSendingLocation {
                        Pair(latitude, longitude)
                    }
                } ?: run {
                    Log.e("Location", "Location is null")
                }
            } catch (e: SecurityException) {
                Log.e("Location", "Permission denied: ${e.message}")
            }
        }
    }

    // Ruxsat natijasini qayta ishlash
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSendingLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun getLastKnownLocation(callback: (android.location.Location?) -> Unit) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
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
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
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
            "arrived_to_customer"
        )

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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopSendingLocation()
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



}