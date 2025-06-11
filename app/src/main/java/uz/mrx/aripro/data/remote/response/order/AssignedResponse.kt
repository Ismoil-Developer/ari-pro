package uz.mrx.aripro.data.remote.response.order

data class AssignedResponse(
    val id: Int,
    val order_code: String,
    val shop_title: String,
    val shop_id: String,
    val items: String,
    val created_at: String,
    val status: String
)