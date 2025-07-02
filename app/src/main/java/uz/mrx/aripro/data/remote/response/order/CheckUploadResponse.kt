package uz.mrx.aripro.data.remote.response.order

import com.google.gson.annotations.SerializedName

data class CheckUploadResponse(
    val id: Int,
    val order: Int,
    val image: String,
    @SerializedName("qr_url")
    val qrUrl: String,
    @SerializedName("uploaded_at")
    val uploadedAt: String
)
