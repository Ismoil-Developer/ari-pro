package uz.mrx.aripro.data.remote.response.order

data class OrderDetailResponse(
    val items: String,
    val allow_other_shops: Boolean,
    val house_number: String,
    val apartment_number: String,
    val floor: Int,
    val intercom_code: String,
    val additional_note: String,
    val shop: Shop
)

data class Shop(
    val title: String,
    val image: String
)
