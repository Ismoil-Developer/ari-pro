package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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

    private var order: OrderActiveResponse? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            courierWebSocketClient.startSendingLocationUpdates(requireContext())
        } else {
            Toast.makeText(requireContext(), "Joylashuv ruxsat berilmadi", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Inject
    lateinit var courierWebSocketClient: CourierWebSocketClient

    private var phone: String? = null
    private var orderId: Int? = null
    private var currentDirection: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val directions = listOf(
        "en_route_to_store",
        "arrived_at_store",
        "picked_up",
        "en_route_to_customer",
        "arrived_to_customer"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courierWebSocketClient.connect(
            "ws://ari-delivery.uz/ws/pro/connect/",
            sharedPref.token
        )

        viewModel.connectWebSocket(
            "ws://ari-delivery.uz/ws/pro/connect/",
            sharedPref.token
        )


        binding.icBack.setOnClickListener {
            viewModel.openMainScreen()
        }

        checkAndRequestLocationPermission()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.openMainScreen()
            }
        })

        binding.gps.setOnClickListener {

            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val settingsClient =
                LocationServices.getSettingsClient(requireActivity())
            val task = settingsClient.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // GPS yoqilgan, joylashuvni olish
                getLastKnownLocation { location ->
                    if (location != null) {
                        val userLocation = Point(location.latitude, location.longitude)
                        binding.mapView.map.move(
                            CameraPosition(userLocation, 18.0f, 0.0f, 0.0f),
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

            task.addOnFailureListener { exception ->
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    // GPSni yoqish oynasini ko'rsatish
                    try {
                        exception.startResolutionForResult(requireActivity(), 1001)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(
                            requireContext(),
                            "GPSni yoqish muammosi yuz berdi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "GPSni yoqish talab qilinadi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.message.setOnClickListener { viewModel.openChatScreen() }

        binding.call.setOnClickListener { phone?.let { dialNumber(it) } }

        binding.detail.setOnClickListener {
            orderId?.let {
                viewModel.openOrderDetailScreen(it)
            }
        }

        binding.orderCancel.setOnClickListener {
            orderId?.let { viewModel.orderCancelScreen(it) }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnContinue.setOnClickListener {
            val order = this.order
            if (order == null) {
                Toast.makeText(
                    requireContext(),
                    "Buyurtma maʼlumotlari mavjud emas",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    openMapWithDirections(
                        location.latitude,
                        location.longitude,
                        order.shopLocation.latitude,
                        order.shopLocation.longitude,
                        order.customerLocation.latitude,
                        order.customerLocation.longitude
                    )
                } else {
                    Toast.makeText(requireContext(), "Joylashuv olinmadi", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Joylashuv olishda xatolik", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        binding.swipeView.setOnSwipeListener {
            val next = getNextDirection(currentDirection ?: return@setOnSwipeListener)

            // Agar hozirgi direction "picked_up" bo‘lsa va endi "en_route_to_customer" ga o‘tayotgan bo‘lsa:
            if (currentDirection == "picked_up") {
                orderId?.let {
                    viewModel.openPaymentConfirmScreen(it)
                }
            }

            if (next != null) {
                orderId?.let { viewModel.postDirection(it, DirectionRequest(next)) }
            } else {
                Toast.makeText(requireContext(), "Buyurtma yakunlandi", Toast.LENGTH_SHORT).show()
                orderId?.let { viewModel.openOrderCompletedScreen(it) }
            }
        }

        observeOrderState()
        observeDirectionUpdates()
        observeErrors()

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


    private fun openMapWithDirections(
        courierLat: Double,
        courierLng: Double,
        shopLat: Double,
        shopLng: Double,
        customerLat: Double,
        customerLng: Double
    ) {
        val googleUri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=$courierLat,$courierLng" +
                    "&waypoints=$shopLat,$shopLng" +
                    "&destination=$customerLat,$customerLng" +
                    "&travelmode=driving"
        )

        val googleIntent = Intent(Intent.ACTION_VIEW, googleUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        val yandexUri =
            Uri.parse("yandexmaps://maps.yandex.com/?rtext=$courierLat,$courierLng~$shopLat,$shopLng~$customerLat,$customerLng&rtt=auto")

        val yandexIntent = Intent(Intent.ACTION_VIEW, yandexUri).apply {
            setPackage("ru.yandex.yandexmaps")
        }

        val chooserIntent = Intent.createChooser(googleIntent, "Xaritani tanlang")
        val additionalIntents = mutableListOf<Intent>()

        if (yandexIntent.resolveActivity(requireContext().packageManager) != null) {
            additionalIntents.add(yandexIntent)
        }

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, additionalIntents.toTypedArray())
        startActivity(chooserIntent)

    }

    private fun updateOrderUI(order: OrderActiveResponse) {

        this.order = order

        binding.emptyContainer.visibility = View.GONE
        binding.deliverContainer.visibility = View.VISIBLE

        orderId = order.id
        currentDirection = order.direction
        phone = order.deliverUser?.phoneNumber

        binding.courierName.text = order.deliverUser?.fullName
        binding.courierRating.text = order.deliverUser?.rating.toString()
        binding.swipeView.setText(getButtonText(order.direction))

        updateDeliverySteps(order.direction)


        order.courierLocation?.let {
            val lat = it.latitude
            val lon = it.longitude

        }

        order.customerLocation?.let {
            val lat = it.latitude
            val lon = it.longitude

        }

        order.shopLocation?.let {
            val lat = it.latitude
            val lon = it.longitude
        }


        Glide.with(requireContext())
            .load(order.deliverUser?.avatar)
            .into(binding.prf)

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
            courierWebSocketClient.startSendingLocationUpdates(requireContext())
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeErrorResponse.collectLatest {

                    val isNoActiveOrder = it.contains("No active order", ignoreCase = true)

                    val message = if (isNoActiveOrder) {
                        "Sizda hozirda faol zakaz mavjud emas"
                    } else {
                        "Xatolik yuz berdi: $it"
                    }

                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    // View holatini yangilash
                    if (isNoActiveOrder) {
                        binding.deliverContainer.visibility = View.GONE
                        binding.emptyContainer.visibility = View.VISIBLE
                    } else {
                        binding.deliverContainer.visibility = View.VISIBLE
                        binding.emptyContainer.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun getButtonText(direction: String): String {
        return when (direction) {
            "en_route_to_store" -> "Do‘konga yo‘lda"
            "arrived_at_store" -> "Do‘konga yetib keldi"
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            Toast.makeText(requireContext(), "Joylashuv ruxsati talab qilinadi", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
