package uz.mrx.aripro.presentation.ui.viewmodel.login

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.RegisterResponse

interface LoginScreenViewModel {

    fun openConfirmScreen(phoneNumber:String, code:String)

    fun postRegister(request: RegisterRequest)

    val registerResponse: Flow<RegisterResponse>

    val toastMessage: Flow<String>

}