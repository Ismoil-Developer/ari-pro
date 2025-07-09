package uz.mrx.aripro.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.history.OrderHistoryDetailResponse
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.CheckUploadResponse
import uz.mrx.aripro.data.remote.response.order.DeliveryWeeklyPriceResponse
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.response.order.OrderCancelResponse
import uz.mrx.aripro.data.remote.response.order.OrderFeedBackResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse

interface OrderApi {

    @Multipart
    @POST("/pro/checks/upload/")
    suspend fun uploadCheck(
        @Part("order") order: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("qr_url") qrUrl: RequestBody
    ): Response<CheckUploadResponse>

    @Multipart
    @POST("/pro/manual-checks/upload/")
    suspend fun uploadCheckManual(
        @Part("order") order: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("price") price: RequestBody
    ): Response<CheckUploadResponse>

    @GET("/pro/active/orders/{id}")
    suspend fun getOrderActive(@Path("id") id: Int): Response<OrderActiveResponse>

    @GET("/pro/active/orders/")
    suspend fun getOrderActiveToken(): Response<OrderActiveResponse>

    @POST("/pro/deliver-active/")
    suspend fun postDeliveryActive():Response<WorkActiveResponse>

    @POST("/pro/order/{id}/direction/")
    suspend fun postDirection(@Path("id") id:Int, @Body directionRequest: DirectionRequest):Response<DirectionResponse>

    @GET("/pro/orders/assigned/")
    suspend fun getAssigned():Response<List<AssignedResponse>>

    @POST("/pro/order/{id}/feedback/")
    suspend fun postFeedBack(@Path("id") id: Int, @Body request: OrderFeedBackRequest):Response<OrderFeedBackResponse>

    @POST("/pro/order/{id}/cancel/")
    suspend fun cancelOrder(@Path("id") id: Int, @Body request: OrderCancelRequest):Response<OrderCancelResponse>

    @GET("/pro/orders/history/")
    suspend fun getHistory():Response<List<OrderHistoryResponse>>

    @GET("/pro/orders/history/{id}/")
    suspend fun getHistoryById(@Path("id") id: Int):Response<OrderHistoryDetailResponse>

    @GET("/pro/courier/weekly-earnings/")
    suspend fun getWeeklyEarnings():Response<DeliveryWeeklyPriceResponse>

}