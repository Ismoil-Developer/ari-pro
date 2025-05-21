package uz.mrx.aripro.data.repository.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.utils.ResultData

interface OrderRepository {

    fun connectWebSocket(url: String, token: String)

    fun observeMessages(): Flow<WebSocketOrderEvent>

    fun acceptOrder(orderId: Int)

    fun rejectOrder(orderId: Int)

    fun disconnectWebSocket()

    suspend fun getOrderActive():Flow<ResultData<OrderActiveResponse>>

    suspend fun postDeliveryActive():Flow<ResultData<WorkActiveResponse>>


}
