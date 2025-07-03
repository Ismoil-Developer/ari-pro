package uz.mrx.aripro.presentation.direction.order

interface OrderDeliveryScreenDirection {

    suspend fun openChatScreen()

    suspend fun openOrderCompletedScreen(id: Int)

    suspend fun openOrderDetail(id:Int)

    suspend fun openOrderCancelScreen(id: Int)

    suspend fun openPaymentConfirmScreen(id: Int)

}