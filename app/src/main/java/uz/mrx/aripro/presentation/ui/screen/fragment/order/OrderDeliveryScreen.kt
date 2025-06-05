package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.databinding.ScreenOrderDeliveryBinding
import uz.mrx.aripro.presentation.ui.viewmodel.orderpage.OrderDeliveryScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.orderpage.impl.OrderDeliveryScreenViewModelImpl

@AndroidEntryPoint
class OrderDeliveryScreen : Fragment(R.layout.screen_order_delivery) {

    private val binding: ScreenOrderDeliveryBinding by viewBinding(ScreenOrderDeliveryBinding::bind)
    private val viewModel: OrderDeliveryScreenViewModel by viewModels<OrderDeliveryScreenViewModelImpl>()

    private var phone: String? = null
    private var orderId: Int? = null
    private var currentDirection: String? = null

    private val directions = listOf(
        "en_route_to_store",
        "arrived_at_store",
        "picked_up",
        "en_route_to_customer",
        "arrived_to_customer",
        "handed_over"
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            "arrived_to_customer",
            "handed_over"
        )

        val nextIndex = directions.indexOf(direction) + 1
        val nextDirection = directions.getOrNull(nextIndex)

        return when (nextDirection) {
            "arrived_at_store" -> "Do‘konga yetib keldi"
            "picked_up" -> "Yukni oldi"
            "en_route_to_customer" -> "Mijozga yo‘lda"
            "arrived_to_customer" -> "Manziliga yetib keldi"
            "handed_over" -> "Mahsulot topshirildi"
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
            }
            "handed_over" -> {
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