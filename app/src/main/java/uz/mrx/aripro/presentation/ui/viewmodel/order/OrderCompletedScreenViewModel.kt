package uz.mrx.aripro.presentation.ui.viewmodel.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.data.remote.response.order.OrderFeedBackResponse

interface OrderCompletedScreenViewModel {

    fun openMainScreen()

    fun postFeedBack(id:Int, request: OrderFeedBackRequest)

    val feedBackResponse:Flow<OrderFeedBackResponse>

}