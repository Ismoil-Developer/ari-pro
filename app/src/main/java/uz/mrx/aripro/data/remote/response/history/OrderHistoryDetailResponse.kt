package uz.mrx.aripro.data.remote.response.history

data class OrderHistoryDetailResponse(
    val total_price: String?,      // nullable, chunki qiymati null bo'lishi mumkin
    val item_price: String?,       // nullable, chunki qiymati null bo'lishi mumkin
    val delivery_price: String,
    val order_code: String,
    val created_at: String,
    val shop_id: Int,
    val shop_title: String,
    val shop_image: String
)
