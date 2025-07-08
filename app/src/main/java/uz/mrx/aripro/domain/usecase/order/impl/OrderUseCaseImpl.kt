package uz.mrx.aripro.domain.usecase.order.impl

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.history.OrderHistoryDetailResponse
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.CheckUploadResponse
import uz.mrx.aripro.data.remote.response.order.DeliveryWeeklyPriceResponse
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.OrderCancelResponse
import uz.mrx.aripro.data.remote.response.order.OrderFeedBackResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.data.repository.order.OrderRepository
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.utils.ResultData
import java.io.File
import javax.inject.Inject

class OrderUseCaseImpl @Inject constructor(private val repository: OrderRepository):OrderUseCase {


    override suspend fun uploadCheck(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ): Flow<ResultData<CheckUploadResponse>> = repository.uploadCheck(orderId, imageFile, price)

   override suspend fun uploadCheckManual(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ): Flow<ResultData<CheckUploadResponse>> = repository.uploadCheckManual(orderId, imageFile, price)



//    override fun startLocationUpdates(locationProvider: suspend () -> Pair<Double, Double>) =
//        repository.startLocationUpdates(locationProvider)

//    override fun stopLocationUpdates() = repository.stopLocationUpdates()

    override fun connectWebSocket(url: String, token: String) = repository.connectWebSocket(url, token)

    override fun observeMessages(): Flow<WebSocketOrderEvent> = repository.observeMessages()

    override fun acceptOrder(orderId: Int) = repository.acceptOrder(orderId)

    override fun rejectOrder(orderId: Int) = repository.rejectOrder(orderId)

    override fun disconnectWebSocket() = repository.disconnectWebSocket()



    override suspend fun getOrderActive(id: Int): Flow<ResultData<OrderActiveResponse>> = repository.getOrderActive(id)

    override suspend fun getOrderActiveToken(): Flow<ResultData<OrderActiveResponse>> = repository.getOrderActiveToken()

    override suspend fun postDeliveryActive(): Flow<ResultData<WorkActiveResponse>> = repository.postDeliveryActive()

    override suspend fun postDirection(
        id: Int,
        request: DirectionRequest
    ): Flow<ResultData<DirectionResponse>> = repository.postDirection(id, request)

    override suspend fun getAssignedOrder(): Flow<ResultData<List<AssignedResponse>>> = repository.getAssignedOrder()

    override suspend fun cancelOrder(
        id: Int,
        request: OrderCancelRequest
    ): Flow<ResultData<OrderCancelResponse>> = repository.cancelOrder(id, request)

    override suspend fun postFeedBack(
        id: Int,
        request: OrderFeedBackRequest
    ): Flow<ResultData<OrderFeedBackResponse>> = repository.postFeedBack(id, request)

    override suspend fun getHistory(): Flow<ResultData<List<OrderHistoryResponse>>> = repository.getHistory()

    override suspend fun getHistoryById(id: Int): Flow<ResultData<OrderHistoryDetailResponse>> = repository.getHistoryById(id)

    override suspend fun getDeliveryWeeklyPrice(): Flow<ResultData<DeliveryWeeklyPriceResponse>> = repository.getDeliveryWeeklyPrice()

}