package uz.mrx.aripro.data.remote.response.profile


data class ProfileResponse(
    val avatar: String?,          // null bo'lishi mumkin
    val full_name: String?,       // null bo'lishi mumkin
    val deliver_id: String,       // majburiy maydon
    val balance: String,          // string formatda, masalan "0.00"
    val phone_number: String,     // telefon raqami
    val work_start: String,       // ish boshlanish vaqti, masalan "06:00:00"
    val work_end: String          // ish tugash vaqti, masalan "04:59:00"
)
