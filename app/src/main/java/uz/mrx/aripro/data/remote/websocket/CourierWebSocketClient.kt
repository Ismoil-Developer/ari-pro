package uz.mrx.aripro.data.remote.websocket

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import uz.mrx.aripro.utils.ResultData
import uz.mrx.aripro.utils.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourierWebSocketClient @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    private var webSocket: WebSocket? = null

    private val _incomingOrders = flow<WebSocketOrderEvent.NewOrder>()
    val incomingOrders = _incomingOrders.asSharedFlow()  // Exposing the flow

    private val _orderTimeouts = flow<WebSocketOrderEvent.OrderTimeout>()
    val orderTimeouts = _orderTimeouts.asSharedFlow()  // Exposing the flow

    private val _orderTakens = flow<WebSocketOrderEvent.OrderTaken>()
    val orderTakens = _orderTakens.asSharedFlow()

    private var currentUrl: String? = null
    private var currentToken: String? = null

    fun connect(url: String, token: String) {

        if (webSocket != null) return // already connected

        currentUrl = url
        currentToken = token

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Message received: $text")
                parseMessage(text).onSuccess { event ->
                    when (event) {
                        is WebSocketOrderEvent.NewOrder -> {
                            _incomingOrders.tryEmit(event)  // Emit to the shared flow
                            Log.d("WebSocket", "New Order event: $event")
                        }
                        is WebSocketOrderEvent.OrderTaken -> {
                            _orderTakens.tryEmit(event)
                            Log.d("WebSocket", "Order Taken event: $event")
                        }
                        is WebSocketOrderEvent.OrderTimeout -> {
                            Log.d("WebSocket", "Order Timeout event: $event")
                            _orderTimeouts.tryEmit(event)  // Emit to the shared flow
                        }
                        is WebSocketOrderEvent.UnknownMessage -> {
                            // Handle unknown messages if needed
                            Log.d("WebSocket", "Unknown message: $event")
                        }
                    }
                }.onError { error ->
                    Log.e("WebSocket", "Parsing error: ${error.localizedMessage}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.localizedMessage}")
                webSocket.cancel()
                this@CourierWebSocketClient.webSocket = null
                reconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: $reason")
                this@CourierWebSocketClient.webSocket = null
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }

    private fun reconnect() {
        currentUrl?.let { url ->
            currentToken?.let { token ->
                connect(url, token)
            }
        }
    }

    private fun parseMessage(text: String): ResultData<WebSocketOrderEvent> {
        return try {
            val json = JSONObject(text)

            when {
                json.has("shop") && json.has("order_items") -> {
                    val orderId = json.getInt("order_id")
                    val shop = json.getString("shop")
                    val details = json.optString("details", "")
                    val price = if (json.isNull("price")) null else json.getDouble("price")
                    val distanceKm = json.optDouble("distance_km", 0.0)
                    val durationMin = json.optDouble("duration_min", 0.0)
                    val orderItems = json.optString("order_items", "")

                    // coordinates optional bo'lishi mumkin
                    val coordinates = if (json.has("coordinates")) {
                        val coordsJson = json.getJSONArray("coordinates")
                        listOf(coordsJson.getDouble(0), coordsJson.getDouble(1))
                    } else {
                        emptyList()
                    }

                    ResultData.success(
                        WebSocketOrderEvent.NewOrder(
                            orderId = orderId,
                            shop = shop,
                            coordinates = coordinates,
                            details = details,
                            price = price,
                            distanceKm = distanceKm,
                            durationMin = durationMin,
                            orderItems = orderItems
                        )
                    )
                }

                json.has("type") && json.getString("type") == "order_taken" -> {
                    val orderId = json.getInt("order_id")
                    ResultData.success(WebSocketOrderEvent.OrderTaken(orderId))
                }

                json.has("details") && json.has("order_id") -> {
                    val orderId = json.getInt("order_id")
                    val details = json.getString("details")
                    ResultData.success(WebSocketOrderEvent.OrderTimeout(orderId, details))
                }

                else -> {
                    ResultData.success(WebSocketOrderEvent.UnknownMessage(text))
                }

            }
        } catch (e: Exception) {
            ResultData.error(e)
        }
    }


}
