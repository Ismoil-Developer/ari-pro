package uz.mrx.aripro.domain.usecase.profile

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse
import uz.mrx.aripro.utils.ResultData

interface ProfileUseCase {


    suspend fun getProfile(): Flow<ResultData<DeliveryHomeResponse>>

    suspend fun getProfilePage():Flow<ResultData<ProfileResponse>>

}