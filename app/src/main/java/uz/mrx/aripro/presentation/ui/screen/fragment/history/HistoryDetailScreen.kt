package uz.mrx.aripro.presentation.ui.screen.fragment.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenHistoryDetailBinding
import uz.mrx.aripro.presentation.ui.viewmodel.history.HistoryDetailScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.history.HistoryDetailScreenViewModelImpl

@AndroidEntryPoint
class HistoryDetailScreen:Fragment(R.layout.screen_history_detail) {

    private val binding:ScreenHistoryDetailBinding by viewBinding(ScreenHistoryDetailBinding::bind)
    private val viewModel:HistoryDetailScreenViewModel by viewModels<HistoryDetailScreenViewModelImpl>()
    private val args:HistoryDetailScreenArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (args.id != -1){
            viewModel.getHistoryById(args.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getHistoryByIdResponse.collectLatest {

                binding.shopName.text = it.shop_title
                binding.shopName2.text = it.shop_title
                Glide.with(requireContext()).load(it.shop_image).into(binding.deliveryBasket)

                binding.contractNumber.text =  it.order_code
                binding.date.text = it.created_at


                binding.price.text = it.item_price
                binding.totalCost.text = it.total_price
                binding.courierCost.text = it.delivery_price


            }
        }


    }
}