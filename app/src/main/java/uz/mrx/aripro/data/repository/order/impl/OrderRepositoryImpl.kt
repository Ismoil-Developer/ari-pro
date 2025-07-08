package uz.mrx.aripro.data.repository.order.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.mrx.aripro.data.remote.api.OrderApi
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
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.data.repository.order.OrderRepository
import uz.mrx.aripro.utils.ResultData
import java.io.File
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val webSocketClient: CourierWebSocketClient,
    private val api: OrderApi,
    @ApplicationContext private val context: Context
) : OrderRepository {


    override suspend fun uploadCheck(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ) = channelFlow<ResultData<CheckUploadResponse>> {
        try {
            val orderRequestBody = orderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val priceRequestBody = price.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = prepareFilePart(imageFile)

            val response = api.uploadCheck(
                order = orderRequestBody,
                image = imagePart,
                price = priceRequestBody
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                trySend(ResultData.error(Exception(errorBody ?: "Unknown error")))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }


    override suspend fun uploadCheckManual(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ) = channelFlow<ResultData<CheckUploadResponse>> {
        try {
            val orderRequestBody = orderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val priceRequestBody = price.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = prepareFilePart(imageFile)

            val response = api.uploadCheckManual(
                order = orderRequestBody,
                image = imagePart,
                price = priceRequestBody
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                trySend(ResultData.error(Exception(errorBody ?: "Unknown error")))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }


    private fun prepareFilePart(uri: Uri): MultipartBody.Part {
        val file = File(context.cacheDir, "check_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val requestFile = file
            .asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }


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

//    override fun startLocationUpdates(locationProvider: suspend () -> Pair<Double, Double>) {
//        webSocketClient.startSendingLocationUpdates(locationProvider)
//    }

    // 🔹 Lokatsiyani yuborishni to‘xtatish
//    override fun stopLocationUpdates() {
//        webSocketClient.stopSendingLocationUpdates()
//    }


    override suspend fun getOrderActive(id: Int) = channelFlow<ResultData<OrderActiveResponse>> {
        try {

            val response = api.getOrderActive(id)
            if (response.isSuccessful) {

                val registerResponse = response.body()

                if (registerResponse != null) {
                    trySend(ResultData.success(registerResponse))
                } else {
                    trySend(ResultData.messageText("Something went wrong"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun getOrderActiveToken() = channelFlow<ResultData<OrderActiveResponse>> {
        try {

            val response = api.getOrderActiveToken()
            if (response.isSuccessful) {

                val registerResponse = response.body()

                if (registerResponse != null) {
                    trySend(ResultData.success(registerResponse))
                } else {
                    trySend(ResultData.messageText("Something went wrong"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }


    override suspend fun postDeliveryActive() = channelFlow<ResultData<WorkActiveResponse>> {
        try {

            val response = api.postDeliveryActive()

            if (response.isSuccessful) {

                val registerResponse = response.body()

                if (registerResponse != null) {
                    trySend(ResultData.success(registerResponse))
                } else {
                    trySend(ResultData.messageText("Something went wrong"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun postDirection(
        id: Int,
        request: DirectionRequest
    ) = channelFlow<ResultData<DirectionResponse>> {
        try {

            val response = api.postDirection(id, request)

            if (response.isSuccessful) {

                val registerResponse = response.body()

                if (registerResponse != null) {
                    trySend(ResultData.success(registerResponse))
                } else {
                    trySend(ResultData.messageText("Something went wrong"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun getAssignedOrder() = channelFlow<ResultData<List<AssignedResponse>>> {
        try {
            val response = api.getAssigned()
            if (response.isSuccessful) {
                val newsResponse = response.body() as List<AssignedResponse>

                trySend(ResultData.success(newsResponse))

            } else {
                trySend(ResultData.messageText(response.message()))
            }
        } catch (e: Exception) {
            trySend(ResultData.messageText(e.message.toString()))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun cancelOrder(
        id: Int,
        request: OrderCancelRequest
    ) = channelFlow<ResultData<OrderCancelResponse>> {
        try {
            val response = api.cancelOrder(id, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                trySend(ResultData.messageText("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }

    override suspend fun postFeedBack(
        id: Int,
        request: OrderFeedBackRequest
    ) = channelFlow<ResultData<OrderFeedBackResponse>> {
        try {
            val response = api.postFeedBack(id, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                trySend(ResultData.messageText("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }

    override suspend fun getHistory() = channelFlow<ResultData<List<OrderHistoryResponse>>> {
        try {
            val response = api.getHistory()
            if (response.isSuccessful) {
                val newsResponse = response.body() as List<OrderHistoryResponse>

                trySend(ResultData.success(newsResponse))

            } else {
                trySend(ResultData.messageText(response.message()))
            }
        } catch (e: Exception) {
            trySend(ResultData.messageText(e.message.toString()))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun getHistoryById(id: Int) = channelFlow<ResultData<OrderHistoryDetailResponse>> {
        try {
            val response = api.getHistoryById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                trySend(ResultData.messageText("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }

    override suspend fun getDeliveryWeeklyPrice() = channelFlow<ResultData<DeliveryWeeklyPriceResponse>> {
        try {
            val response = api.getWeeklyEarnings()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(ResultData.success(body))
                } else {
                    trySend(ResultData.messageText("Response body is null"))
                }
            } else {
                trySend(ResultData.messageText("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }


}
