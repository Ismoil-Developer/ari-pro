package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.PaymentConfirmScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.order.PaymentConfirmScreenDirections
import javax.inject.Inject

class PaymentConfirmScreenDirectionImpl @Inject constructor(private val navigator: Navigator) :
    PaymentConfirmScreenDirection {

    override suspend fun openQrScannerFragment(id:Int) {
        navigator.navigateTo(PaymentConfirmScreenDirections.actionPaymentConfirmScreenToQrScannerFragment(id))
    }

}