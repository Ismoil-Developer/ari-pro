package uz.mrx.aripro.presentation.ui.viewmodel.confirm

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse

interface ConfirmScreenViewModel {

    fun openScreen()

    fun postConfirm(request: ConfirmRequest)

    val confirmResponse: Flow<ConfirmResponse>

    val errorToastMessage:Flow<String>

    fun postRegister(request: RegisterRequest)

    val registerResponse: Flow<RegisterResponse>

    val toastMessage: Flow<String>


}