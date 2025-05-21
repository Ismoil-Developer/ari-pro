package uz.mrx.aripro.data.remote.response.profile


data class DeliveryHomeResponse(
    val avatar: String?,         // null bo'lishi mumkin
    val full_name: String?,      // null bo'lishi mumkin
    val deliver_id: String,      // majburiy maydon
    val work_active: Boolean     // majburiy maydon
)
