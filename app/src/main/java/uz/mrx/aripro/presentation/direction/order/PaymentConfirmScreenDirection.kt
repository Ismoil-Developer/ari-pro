package uz.mrx.aripro.presentation.direction.order

interface PaymentConfirmScreenDirection {

    suspend fun openQrScannerFragment(id:Int)

    suspend fun openDeliveryScreen(id:Int)

}