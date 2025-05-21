package uz.mrx.aripro.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse

interface OrderApi {

    @GET("/pro/active/orders/")
    suspend fun getOrderActive(): Response<OrderActiveResponse>

    @POST("/pro/deliver-active/")
    suspend fun postDeliveryActive():Response<WorkActiveResponse>

}