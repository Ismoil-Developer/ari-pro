package uz.mrx.aripro.data.remote.response.order

import com.google.gson.annotations.SerializedName

data class DeliveryWeeklyPriceResponse(
    @SerializedName("since")
    val since: String,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("delivery_price")
    val deliveryPrice: Double
)
