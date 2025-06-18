package uz.mrx.aripro.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import uz.mrx.aripro.data.remote.response.profile.ContactResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse

interface ProfileApi {

    @GET("/pro/deliver-home/")
    suspend fun getProfile():Response<DeliveryHomeResponse>

    @GET("/pro/deliver-profile/")
    suspend fun getProfilePage():Response<ProfileResponse>

    @GET("/goo/contact/")
    suspend fun getContact():Response<ContactResponse>

}