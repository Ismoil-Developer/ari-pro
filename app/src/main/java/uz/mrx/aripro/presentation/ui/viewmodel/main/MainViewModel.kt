package uz.mrx.aripro.presentation.ui.viewmodel.main

import kotlinx.coroutines.flow.SharedFlow
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent

interface MainViewModel {

    val incomingOrders: SharedFlow<WebSocketOrderEvent.NewOrder>
    val orderTimeouts: SharedFlow<WebSocketOrderEvent.OrderTimeout>

    val orderTaken:SharedFlow<WebSocketOrderEvent.OrderTaken>


    fun connectWebSocket(url: String, token: String)

    fun disconnectWebSocket()

    fun acceptOrder(orderId: Int)
    fun rejectOrder(orderId: Int)




}