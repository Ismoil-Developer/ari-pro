package uz.mrx.aripro.domain.usecase.register.impl

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse
import uz.mrx.aripro.data.repository.register.RegisterRepository
import uz.mrx.aripro.domain.usecase.register.RegisterUseCase
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(private val repository: RegisterRepository):RegisterUseCase  {

    override suspend fun register(request: RegisterRequest): Flow<ResultData<RegisterResponse>> = repository.register(request)

    override suspend fun confirm(request: ConfirmRequest): Flow<ResultData<ConfirmResponse>> = repository.confirm(request)

}