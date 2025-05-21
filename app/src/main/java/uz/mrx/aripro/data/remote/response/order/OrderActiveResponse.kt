package uz.mrx.aripro.data.remote.response.order

data class OrderActiveResponse(
    val id: Int,
    val delivered_at: String?, // null bo'lishi mumkin
    val customer_info: CustomerInfo
)

data class CustomerInfo(
    val id: String,
    val avatar: String?, // null bo'lishi mumkin
    val full_name: String,
    val phone_number: String,
    val rating: Double
)
