package uz.mrx.aripro.presentation.ui.viewmodel.login.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.RegisterResponse
import uz.mrx.aripro.domain.usecase.register.RegisterUseCase
import uz.mrx.aripro.presentation.direction.login.LoginScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.login.LoginScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModelImpl @Inject constructor(
    private val direction: LoginScreenDirection,
    private val useCase: RegisterUseCase
) :
    LoginScreenViewModel, ViewModel() {


    private val _toastMessage = MutableSharedFlow<String>(replay = 1)
    override val toastMessage: Flow<String> get() = _toastMessage



    override fun openConfirmScreen(phoneNumber:String, code:String) {
        viewModelScope.launch {
            direction.openConfirmScreen(phoneNumber, code)
        }
    }

    override fun postRegister(request: RegisterRequest) {
        viewModelScope.launch {
            useCase.register(request).collectLatest { result ->
                result.onError { error ->
                    val errorMessage = error.message ?: "Noma'lum xatolik yuz berdi"

                    // JSON ichidan SMS kodini chiqarish
                    val regex = """SMS kod: (\d{5})""".toRegex()
                    val matchResult = regex.find(errorMessage)
                    val smsCode = matchResult?.groupValues?.get(1) ?: "Xatolik"

                    _toastMessage.emit("$smsCode")
                    Log.d("AAAAA", "postRegister: $smsCode")
                }
                result.onMessage { message ->
                    _toastMessage.tryEmit(message.toString())
                }
                result.onSuccess {
                    registerResponse.tryEmit(it)
                    // Faqat muvaffaqiyatli bo‘lsa, keyingi ekran
                }
            }
        }
    }



    override val registerResponse = flow<RegisterResponse>()


}