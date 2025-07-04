package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.LoadData
import uz.mrx.aripro.databinding.PageOrderBinding
import uz.mrx.aripro.presentation.adapter.OrderHistoryAdapter
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderPageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderPageViewModelImpl

@AndroidEntryPoint
class OrderPage:Fragment(R.layout.page_order) {

    private val binding:PageOrderBinding by viewBinding(PageOrderBinding::bind)
    private val viewModel:OrderPageViewModel by viewModels<OrderPageViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = OrderHistoryAdapter{
            viewModel.openHistoryDetailScreen(it.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.historyResponse.collectLatest {
                if (it != null){

                    adapter.submitList(it)

                }
            }
        }

        binding.rv.adapter = adapter

    }
}