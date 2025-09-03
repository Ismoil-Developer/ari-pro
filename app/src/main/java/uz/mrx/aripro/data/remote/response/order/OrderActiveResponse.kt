package uz.mrx.aripro.data.remote.response.order

import com.google.gson.annotations.SerializedName

data class OrderActiveResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("delivery_price")
    val deliveryPrice: String,

    @SerializedName("assigned_at")
    val assignedAt: String,

    @SerializedName("direction")
    val direction: String,

    @SerializedName("additional_shop_active")
    val additionalShopActive: Boolean,

    @SerializedName("direction_additional")
    val directionAdditional: String?,

    @SerializedName("customer_info")
    val customerInfo: CustomerInfo,

    @SerializedName("customer_location")
    val customerLocation: LocationData,

    @SerializedName("shop_location")
    val shopLocation: ShopLocation,

    @SerializedName("courier_location")
    val courierLocation: CourierLocation?
)

data class CustomerInfo(
    @SerializedName("id")
    val id: String,

    @SerializedName("avatar")
    val avatar: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("rating")
    val rating: Double
)

data class LocationData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String?
)

data class ShopLocation(
    @SerializedName("id")
    val id: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("title")
    val title: String?
)

data class CourierLocation(
    @SerializedName("id")
    val id: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("updated_at")
    val updatedAt: String
)
