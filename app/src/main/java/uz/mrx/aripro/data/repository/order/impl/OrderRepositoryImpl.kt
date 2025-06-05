package uz.mrx.aripro.data.repository.order.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import uz.mrx.aripro.data.remote.api.OrderApi
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.data.repository.order.OrderRepository
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val webSocketClient: CourierWebSocketClient,
    private val api: OrderApi
) : OrderRepository {

    override fun connectWebSocket(url: String, token: String) {
        webSocketClient.connect(url, token)
    }

    override fun observeMessages(): Flow<WebSocketOrderEvent> {
        return merge(
            webSocketClient.incomingOrders.onEach {
                Log.d("WebSocketEvent", "Received incoming order: $it")
            },
            webSocketClient.orderTimeouts.onEach {
                Log.d("WebSocketEvent", "Received order timeout: $it")
            },
            webSocketClient.orderTakens.onEach {
                Log.d("WebSocketEvent", "Received order taken: $it")
            }
        ).onEach {
            Log.d("WebSocketEvent", "Merged event: $it")
        }
    }

    override fun acceptOrder(orderId: Int) {
        val message = """
            {
                "action": "accept",
                "order_id": "$orderId"
            }
        """.trimIndent()

        webSocketClient.sendMessage(message)
    }

    override fun rejectOrder(orderId: Int) {
        val message = """
            {
                "action": "reject",
                "order_id": "$orderId"
            }
        """.trimIndent()

        webSocketClient.sendMessage(message)
    }

    override fun disconnectWebSocket() {
        webSocketClient.disconnect()
    }

    override suspend fun getOrderActive() = channelFlow<ResultData<OrderActiveResponse>> {
        try {

            val response = api.getOrderActive()

            if (response.isSuccessful) {

                val profileResponse = response.body()
                if (profileResponse != null) {
                    trySend(ResultData.success(profileResponse))
                } else {
                    close(Exception("No data found for the provided ID"))
                }
            } else {
                val errorMessage = "Error ${response.code()}: ${response.message()}"
                Log.e("API_ERROR", errorMessage)
                close(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception while fetching data by ID: ${e.message}")
            close(e)
        }
    }


    override suspend fun postDeliveryActive(): Flow<ResultData<WorkActiveResponse>> = channelFlow {
        try {
            val response = api.postDeliveryActive()

            if (response.isSuccessful) {
                val deliveryResponse = response.body()
                if (deliveryResponse != null) {
                    trySend(ResultData.success(deliveryResponse))
                } else {
                    close(Exception("No data found in postDeliveryActive response"))
                }
            } else {
                val errorMessage = "Error ${response.code()}: ${response.message()}"
                Log.e("API_ERROR", errorMessage)
                close(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception in postDeliveryActive: ${e.message}")
            close(e)
        }
    }

    override suspend fun postDirection(
        id: Int,
        request: DirectionRequest
    ) = channelFlow<ResultData<DirectionResponse>> {
        try {

            val response = api.postDirection(id, request)

            if (response.isSuccessful){

                val registerResponse = response.body()

                if (registerResponse != null){
                    trySend(ResultData.success(registerResponse))
                }else{
                    trySend(ResultData.messageText("Something went wrong"))
                }
            }else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

}
