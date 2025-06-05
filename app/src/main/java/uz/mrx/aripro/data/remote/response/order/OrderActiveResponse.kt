package uz.mrx.aripro.data.remote.response.order

data class OrderActiveResponse(
    val id: Int,
    val delivery_price: Int,
    val assigned_at: String,
    val direction: String,
    val delivery_duration_min: Double,
    val customer_info: CustomerInfo,
    val customer_location: CustomerLocation,
    val shop_location: ShopLocation,
    val courier_location: CourierLocation
)

data class CustomerInfo(
    val id: String,
    val avatar: String,
    val full_name: String,
    val phone_number: String,
    val rating: Double
)

data class CustomerLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class ShopLocation(
    val latitude: Double,
    val longitude: Double,
    val title: String
)

data class CourierLocation(
    val latitude: Double,
    val longitude: Double,
    val updated_at: String
)
