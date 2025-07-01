package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderCancelViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderCancelViewModelImpl
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.OrderCancelData
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.databinding.ScreenCancelOrderBinding
import uz.mrx.aripro.presentation.adapter.CancelAdapter
import uz.mrx.aripro.presentation.ui.dialog.CancelDialog

@AndroidEntryPoint
class OrderCancelScreen : Fragment(R.layout.screen_cancel_order) {

    private val binding: ScreenCancelOrderBinding by viewBinding(ScreenCancelOrderBinding::bind)
    private val viewModel: OrderCancelViewModel by viewModels<OrderCancelViewModelImpl>()
    private val args: OrderCancelScreenArgs by navArgs()

    lateinit var list: ArrayList<OrderCancelData>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()


        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        var reason = ""

        val adapter = CancelAdapter {
            reason = it.reason
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cancelResponse.collectLatest { response ->
                if (response.status == "success") {

                    val dialog = CancelDialog(requireContext()) {
                        // "Davom etish" bosilganda nima qilish kerakligini yozing
                        viewModel.openMainScreen()
                    }

                    dialog.show()

                }
            }
        }


        binding.btnContinue.setOnClickListener {
            viewModel.cancelOrder(args.id, OrderCancelRequest(reason))
        }

        adapter.submitList(list)

        binding.rvCancelOrder.adapter = adapter

    }

    fun loadData() {
        list = ArrayList()
        list.add(OrderCancelData(1, "Sababsiz"))
        list.add(OrderCancelData(2, "Fikrimni o'zgartirdim"))
        list.add(OrderCancelData(3, "Kuryer bilan bog'lana olmadim"))
        list.add(OrderCancelData(4, "Mahsulotlar kerak bo'lmay qoldi"))
    }

}