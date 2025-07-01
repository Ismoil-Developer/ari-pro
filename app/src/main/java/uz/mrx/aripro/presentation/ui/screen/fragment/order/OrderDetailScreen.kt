package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenOrderDetailBinding

@AndroidEntryPoint
class OrderDetailScreen:Fragment(R.layout.screen_order_detail) {

    private val binding:ScreenOrderDetailBinding by viewBinding(ScreenOrderDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}