package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderCompletedScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.OrderCompletedScreenViewModelImpl
import uz.mrx.aripro.R
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.databinding.ScreenOrderCompletedBinding

@AndroidEntryPoint
class OrderCompletedScreen:Fragment(R.layout.screen_order_completed) {

    private val binding: ScreenOrderCompletedBinding by viewBinding(ScreenOrderCompletedBinding::bind)
    private val viewModel: OrderCompletedScreenViewModel by viewModels<OrderCompletedScreenViewModelImpl>()

    val args:OrderCompletedScreenArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var ratingg = 0

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            println("Tanlangan yulduzlar soni: $rating")
            ratingg = rating.toInt()
        }

        binding.btnSkip.setOnClickListener {
            viewModel.openMainScreen()
        }

        binding.btnSubmit.setOnClickListener {

            val comment = binding.edtNumber.text.toString()

            viewModel.postFeedBack(args.id, OrderFeedBackRequest(ratingg, comment))

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.feedBackResponse.collectLatest {
                if (it.status == "handed_over"){
                    viewModel.openMainScreen()
                }
            }
        }

    }
}