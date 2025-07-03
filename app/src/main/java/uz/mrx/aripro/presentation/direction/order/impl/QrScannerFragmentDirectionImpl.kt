package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.QrScannerFragmentDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.order.QrScannerFragmentDirections
import javax.inject.Inject

class QrScannerFragmentDirectionImpl @Inject constructor(private val navigator: Navigator) :
    QrScannerFragmentDirection {

    override suspend fun openDeliveryScreen() {

        navigator.navigateTo(QrScannerFragmentDirections.actionQrScannerFragmentToOrderDeliveryScreen())

    }

}