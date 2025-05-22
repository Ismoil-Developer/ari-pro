package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenOrderDeliveryBinding
import uz.mrx.aripro.presentation.ui.viewmodel.orderpage.OrderDeliveryScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.orderpage.impl.OrderDeliveryScreenViewModelImpl

@AndroidEntryPoint
class OrderDeliveryScreen : Fragment(R.layout.screen_order_delivery) {

    private val binding: ScreenOrderDeliveryBinding by viewBinding(ScreenOrderDeliveryBinding::bind)
    private val viewModel: OrderDeliveryScreenViewModel by viewModels<OrderDeliveryScreenViewModelImpl>()

    private var phone: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {

            val defaultLat = 41.311081
            val defaultLong = 69.240562

            val uri = Uri.parse("geo:$defaultLat,$defaultLong?q=$defaultLat,$defaultLong(Yetkazib+berish+joyi)")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Bu yerda hech qanday paket belgilanmaydi, foydalanuvchi o'zi tanlaydi
            val chooser = Intent.createChooser(intent, "Xaritani tanlang")

            // Har ehtimolga qarshi tekshirish
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooser)
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
            viewModel.orderActiveResponse.collectLatest {

                it?.customer_info?.let { info ->
                    phone = info.phone_number
                    binding.courierRating.text = info.rating.toString()

                    if (!info.avatar.isNullOrEmpty()) {
                        Glide.with(requireContext()).load(info.avatar).into(binding.prf)
                    }

                    binding.courierName.text = info.full_name
                }


            }
        }

    }
}