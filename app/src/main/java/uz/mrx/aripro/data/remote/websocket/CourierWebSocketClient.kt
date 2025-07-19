package uz.mrx.aripro.data.remote.websocket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
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
    private var currentUrl: String? = null
    private var currentToken: String? = null

    private var locationUpdateJob: Job? = null

    private val _incomingOrders = MutableSharedFlow<WebSocketOrderEvent.NewOrder>(extraBufferCapacity = 1)
    val incomingOrders = _incomingOrders.asSharedFlow()

    private val _orderTimeouts = flow<WebSocketOrderEvent.OrderTimeout>()
    val orderTimeouts = _orderTimeouts.asSharedFlow()

    private val _orderTakens = MutableSharedFlow<WebSocketOrderEvent.OrderTaken>(extraBufferCapacity = 1)
    val orderTakens = _orderTakens.asSharedFlow()

    private val _orderAssigned = MutableSharedFlow<WebSocketOrderEvent.OrderAssigned>(extraBufferCapacity = 1)
    val orderAssigned = _orderAssigned.asSharedFlow()


    private var locationCallback: LocationCallback? = null


    fun connect(url: String, token: String) {
        if (webSocket != null) return

        currentUrl = url
        currentToken = token
    
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "📩 Message: $text")
                parseMessage(text).onSuccess { event ->
                    when (event) {
                        is WebSocketOrderEvent.NewOrder -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                _incomingOrders.emit(event)
                            }                        }
                        is WebSocketOrderEvent.OrderTaken -> _orderTakens.tryEmit(event)
                        is WebSocketOrderEvent.OrderTimeout -> _orderTimeouts.tryEmit(event)
                        is WebSocketOrderEvent.OrderAssigned -> _orderAssigned.tryEmit(event) // ✅ Yangi qo‘shildi
                        else -> Log.d("WebSocket", "ℹ️ Unknown event")
                    }
                }.onError {
                    Log.e("WebSocket", "❌ Parsing error: ${it.localizedMessage}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "❌ Connection failed: ${t.message}")
                this@CourierWebSocketClient.webSocket = null
                reconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "🔌 Closed: $reason")
                this@CourierWebSocketClient.webSocket = null
            }

        })
    }

    private fun reconnect() {
        disconnect()
        currentUrl?.let { url ->
            currentToken?.let { token ->
                connect(url, token)
            }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    @SuppressLint("MissingPermission")
    fun startSendingLocationUpdates(context: Context) {
        locationUpdateJob?.cancel()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val json = JSONObject().apply {
                        put("action", "location_update")
                        put("latitude", location.latitude)
                        put("longitude", location.longitude)
                    }
                    webSocket?.send(json.toString())
                    Log.d("WebSocket", "Location sent: $json")
                } else {
                    Log.e("WebSocket", "Location is null")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )

        locationUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            awaitCancellation()
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
        }
    }

    fun stopSendingLocationUpdates(context: Context) {
        locationUpdateJob?.cancel()
        locationUpdateJob = null

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }


    private fun parseMessage(text: String): ResultData<WebSocketOrderEvent> = try {
        val json = JSONObject(text)

        when {
            json.has("order_id") && json.has("shop") && json.has("order_items") -> {
                val orderId = json.getInt("order_id")
                val shop = json.getString("shop")
                val details = json.optString("details", "")
                val price = if (json.isNull("price")) null else json.getDouble("price")
                val distanceKm = json.optDouble("distance_km", 0.0)
                val durationMin = json.optDouble("duration_min", 0.0)
                val orderItems = json.optString("order_items", "")

                val coordinates = if (json.has("coordinates")) {
                    val coords = json.getJSONArray("coordinates")
                    listOf(coords.getDouble(0), coords.getDouble(1))
                } else emptyList()

                ResultData.success(
                    WebSocketOrderEvent.NewOrder(
                        orderId, shop, coordinates, details, price, distanceKm, durationMin, orderItems
                    )
                )
            }


            json.optString("type") == "order_taken" -> {
                ResultData.success(WebSocketOrderEvent.OrderTaken(json.getInt("order_id")))
            }

            json.optString("type") == "order.assigned" -> {
                ResultData.success(WebSocketOrderEvent.OrderAssigned(json.getInt("order_id")))
            }

            json.has("details") && json.has("order_id") -> {
                ResultData.success(
                    WebSocketOrderEvent.OrderTimeout(
                        json.getInt("order_id"),
                        json.getString("details")
                    )
                )
            }

            else -> ResultData.success(WebSocketOrderEvent.UnknownMessage(text))
        }

    } catch (e: Exception) {
        ResultData.error(e)
    }

}
