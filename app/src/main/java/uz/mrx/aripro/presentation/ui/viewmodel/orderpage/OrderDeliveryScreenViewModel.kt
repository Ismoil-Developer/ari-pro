package uz.mrx.aripro.presentation.ui.viewmodel.orderpage

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse

interface OrderDeliveryScreenViewModel {

    val orderActiveResponse:Flow<OrderActiveResponse>

}