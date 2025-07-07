package uz.mrx.aripro.data.remote.response.order

import com.google.gson.annotations.SerializedName

data class OrderActiveResponse(
    val id: Int,
    @SerializedName("delivery_price")
    val deliveryPrice: String,
    @SerializedName("assigned_at")
    val assignedAt: String,
    val direction: String,
    @SerializedName("delivery_duration_min")
    val deliveryDurationMin: Double,
    @SerializedName("customer_info")
    val deliverUser: DeliverUser?, // null bo'lishi mumkinligi sababli ? qo'yilgan
    @SerializedName("customer_location")
    val customerLocation: LocationInfo,
    @SerializedName("shop_location")
    val shopLocation: ShopLocation,
    @SerializedName("courier_location")
    val courierLocation: CourierLocation
)

data class DeliverUser(
    val id: String,
    val avatar: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val rating: Double
)

data class LocationInfo(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class ShopLocation(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String
)

data class CourierLocation(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("updated_at")
    val updatedAt: String
)
