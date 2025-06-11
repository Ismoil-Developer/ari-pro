package uz.mrx.aripro.domain.usecase.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.utils.ResultData

interface OrderUseCase {

    fun connectWebSocket(url: String, token: String)

    fun observeMessages(): Flow<WebSocketOrderEvent>

    fun acceptOrder(orderId: Int)

    fun rejectOrder(orderId: Int)

    fun disconnectWebSocket()

    suspend fun getOrderActive():Flow<ResultData<OrderActiveResponse>>

    suspend fun postDeliveryActive():Flow<ResultData<WorkActiveResponse>>

    suspend fun postDirection(id:Int, request: DirectionRequest):Flow<ResultData<DirectionResponse>>

    suspend fun getAssignedOrder():Flow<ResultData<List<AssignedResponse>>>


}