package uz.mrx.aripro.domain.usecase.profile.impl

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.profile.ContactResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse
import uz.mrx.aripro.data.repository.profile.ProfileRepository
import uz.mrx.aripro.domain.usecase.profile.ProfileUseCase
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class ProfileUseCaseImpl @Inject constructor(private val repository: ProfileRepository):
    ProfileUseCase {

    override suspend fun getProfile(): Flow<ResultData<DeliveryHomeResponse>> = repository.getProfile()

    override suspend fun getProfilePage(): Flow<ResultData<ProfileResponse>> = repository.getProfilePage()

    override suspend fun getContact(): Flow<ResultData<ContactResponse>> = repository.getContact()
}