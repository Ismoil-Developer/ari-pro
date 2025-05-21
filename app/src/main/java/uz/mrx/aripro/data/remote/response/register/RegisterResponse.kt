package uz.mrx.aripro.data.remote.response.register

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("non_field_errors") val errors: List<String>? = null
)
