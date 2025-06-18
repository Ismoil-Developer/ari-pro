package uz.mrx.aripro.data.repository.profile

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.profile.ContactResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse
import uz.mrx.aripro.utils.ResultData

interface ProfileRepository {

    suspend fun getProfile(): Flow<ResultData<DeliveryHomeResponse>>


    suspend fun getProfilePage():Flow<ResultData<ProfileResponse>>

    suspend fun getContact():Flow<ResultData<ContactResponse>>

}