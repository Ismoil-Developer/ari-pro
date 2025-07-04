package uz.mrx.aripro.data.remote.response.history

data class OrderHistoryResponse(
    val id: Int,
    val order_code: String,
    val created_at: String,
    val status: String
)