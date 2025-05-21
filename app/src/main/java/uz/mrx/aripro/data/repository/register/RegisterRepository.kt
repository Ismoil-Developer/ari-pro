package uz.mrx.aripro.data.repository.register

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse
import uz.mrx.aripro.utils.ResultData

interface RegisterRepository {

    suspend fun register(request: RegisterRequest): Flow<ResultData<RegisterResponse>>

    suspend fun confirm(request: ConfirmRequest): Flow<ResultData<ConfirmResponse>>

}