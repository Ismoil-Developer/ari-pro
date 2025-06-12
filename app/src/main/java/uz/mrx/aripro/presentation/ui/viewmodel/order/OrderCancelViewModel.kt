package uz.mrx.aripro.presentation.ui.viewmodel.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.data.remote.response.order.OrderCancelResponse

interface OrderCancelViewModel {

    fun cancelOrder(id:Int, request: OrderCancelRequest)

    val cancelResponse:Flow<OrderCancelResponse>

    fun openMainScreen()


}