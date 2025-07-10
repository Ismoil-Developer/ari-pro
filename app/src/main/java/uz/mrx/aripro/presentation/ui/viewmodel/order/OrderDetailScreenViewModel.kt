package uz.mrx.aripro.presentation.ui.viewmodel.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.order.OrderDetailResponse

interface OrderDetailScreenViewModel {

    val orderDetailResponse:Flow<OrderDetailResponse>

    fun getOrderDetail(id:Int)

}