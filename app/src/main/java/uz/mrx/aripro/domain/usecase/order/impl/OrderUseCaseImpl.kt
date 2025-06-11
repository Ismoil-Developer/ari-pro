package uz.mrx.aripro.domain.usecase.order.impl

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.data.repository.order.OrderRepository
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class OrderUseCaseImpl @Inject constructor(private val repository: OrderRepository):OrderUseCase {

    override fun connectWebSocket(url: String, token: String) = repository.connectWebSocket(url, token)

    override fun observeMessages(): Flow<WebSocketOrderEvent> = repository.observeMessages()

    override fun acceptOrder(orderId: Int) = repository.acceptOrder(orderId)

    override fun rejectOrder(orderId: Int) = repository.rejectOrder(orderId)

    override fun disconnectWebSocket() = repository.disconnectWebSocket()

    override suspend fun getOrderActive(): Flow<ResultData<OrderActiveResponse>> = repository.getOrderActive()

    override suspend fun postDeliveryActive(): Flow<ResultData<WorkActiveResponse>> = repository.postDeliveryActive()

    override suspend fun postDirection(
        id: Int,
        request: DirectionRequest
    ): Flow<ResultData<DirectionResponse>> = repository.postDirection(id, request)

    override suspend fun getAssignedOrder(): Flow<ResultData<List<AssignedResponse>>> = repository.getAssignedOrder()

}