package uz.mrx.aripro.presentation.ui.viewmodel.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse

interface OrderDeliveryScreenViewModel {

    val orderActiveResponse:Flow<OrderActiveResponse>

    fun postDirection(id:Int, request: DirectionRequest)

    val responseDirection:Flow<DirectionResponse>

    fun getOrderActive(id:Int)

    val orderActiveToken:Flow<OrderActiveResponse>


    fun openOrderCompletedScreen(id: Int)

    fun openOrderDetailScreen(id: Int)

    fun orderCancelScreen(id: Int)

    fun openChatScreen()


    fun startSendingLocation(locationProvider: suspend () -> Pair<Double, Double>)

    fun stopSendingLocation()


    fun connectWebSocket(url: String, token: String)
    fun disconnectWebSocket()





}