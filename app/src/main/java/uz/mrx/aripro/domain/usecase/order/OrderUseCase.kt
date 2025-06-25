package uz.mrx.aripro.domain.usecase.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.OrderCancelResponse
import uz.mrx.aripro.data.remote.response.order.OrderFeedBackResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.utils.ResultData

interface OrderUseCase {

    fun connectWebSocket(url: String, token: String)

    fun observeMessages(): Flow<WebSocketOrderEvent>

    fun acceptOrder(orderId: Int)

    fun rejectOrder(orderId: Int)

    fun disconnectWebSocket()

//    fun startLocationUpdates(locationProvider: suspend () -> Pair<Double, Double>)
//    fun stopLocationUpdates()

    suspend fun getOrderActive(id: Int):Flow<ResultData<OrderActiveResponse>>

    suspend fun getOrderActiveToken():Flow<ResultData<OrderActiveResponse>>

    suspend fun postDeliveryActive():Flow<ResultData<WorkActiveResponse>>

    suspend fun postDirection(id:Int, request: DirectionRequest):Flow<ResultData<DirectionResponse>>

    suspend fun getAssignedOrder():Flow<ResultData<List<AssignedResponse>>>

    suspend fun cancelOrder(id: Int, request: OrderCancelRequest):Flow<ResultData<OrderCancelResponse>>

    suspend fun postFeedBack(id: Int, request: OrderFeedBackRequest):Flow<ResultData<OrderFeedBackResponse>>


}