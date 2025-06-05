package uz.mrx.aripro.presentation.ui.viewmodel.homepage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent

interface HomePageViewModel {

    val incomingOrders: SharedFlow<WebSocketOrderEvent.NewOrder>
    val orderTimeouts: SharedFlow<WebSocketOrderEvent.OrderTimeout>

    val orderTaken:SharedFlow<WebSocketOrderEvent.OrderTaken>

    fun connectWebSocket(url: String, token: String)
    fun disconnectWebSocket()

    fun acceptOrder(orderId: Int)
    fun rejectOrder(orderId: Int)

    val profileResponse: Flow<DeliveryHomeResponse>

    fun openOrderDetailScreen()

    fun openOrderDeliveryScreen()

    fun postDeliveryActive()

    val deliveryActiveResponse:Flow<WorkActiveResponse>

}