package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenOrderDetailBinding
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderDetailScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderDetailScreenViewModelImpl

@AndroidEntryPoint
class OrderDetailScreen:Fragment(R.layout.screen_order_detail) {

    private val binding:ScreenOrderDetailBinding by viewBinding(ScreenOrderDetailBinding::bind)
    private val args:OrderDetailScreenArgs by navArgs()
    private val viewModel:OrderDetailScreenViewModel by viewModels<OrderDetailScreenViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        Log.d("IIII", "onViewCreated: ${args.id}")

        if (args.id != -1){
            viewModel.getOrderDetail(args.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderDetailResponse.collectLatest {

                binding.edtOrder.text = it.items

                Glide.with(requireContext()).load(it.shop.image).into(binding.payTypeImg)

                binding.typeName.text = it.shop.title

                binding.edtOrder.text = it.items
                binding.floor.text = "Qavat: "+it.floor.toString()
                binding.damophone.text = "Damofon: "+it.intercom_code
                binding.appartmentNumber.text = "Podyezd: " + it.apartment_number
                binding.houseNumber.text = "Uy raqami: "+it.house_number
                binding.otherMessage.text = it.additional_note

            }
        }
    }
}