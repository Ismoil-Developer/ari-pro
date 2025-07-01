package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenPaymentConfirmBinding

@AndroidEntryPoint
class PaymentConfirmScreen:Fragment(R.layout.screen_payment_confirm) {

    private val binding:ScreenPaymentConfirmBinding by viewBinding(ScreenPaymentConfirmBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}