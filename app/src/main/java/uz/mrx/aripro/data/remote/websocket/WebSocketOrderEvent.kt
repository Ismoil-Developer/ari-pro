package uz.mrx.aripro.data.remote.websocket

import com.google.gson.annotations.SerializedName


sealed class WebSocketOrderEvent {

    data class NewOrder(
        val orderId: Int,
        val shop: String,
        val coordinates: List<Double>,
        val details: String,
        val price: Double?,          // yangi maydon
        @SerializedName("distance_km") val distanceKm: Double,
        @SerializedName("duration_min") val durationMin: Double,
        @SerializedName("order_items") val orderItems: String     // yangi maydon
    ) : WebSocketOrderEvent()

    data class OrderTimeout(
        val orderId: Int,
        val details: String
    ) : WebSocketOrderEvent()

    data class OrderTaken(
        val orderId: Int
    ) : WebSocketOrderEvent()

    data class OrderAssigned(
        val orderId: Int
    ) : WebSocketOrderEvent()

    data class UnknownMessage(
        val rawMessage: String
    ) : WebSocketOrderEvent()
}
