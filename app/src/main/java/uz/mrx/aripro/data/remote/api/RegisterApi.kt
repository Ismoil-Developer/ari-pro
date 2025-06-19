package uz.mrx.aripro.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse

interface RegisterApi {

    @POST("/pro/send-code/")
    suspend fun postRegister(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/pro/verify-code/")
    suspend fun postConfirm(@Body request: ConfirmRequest): Response<ConfirmResponse>

}