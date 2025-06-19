package uz.mrx.aripro.presentation.ui.viewmodel.confirm.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse
import uz.mrx.aripro.domain.usecase.register.RegisterUseCase
import uz.mrx.aripro.presentation.direction.confirm.ConfirmScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.confirm.ConfirmScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class ConfirmScreenViewModelImpl @Inject constructor(
    private val direction: ConfirmScreenDirection,
    private val useCase: RegisterUseCase
) : ConfirmScreenViewModel, ViewModel() {

    override fun openScreen() {
        viewModelScope.launch {
            direction.openMainScreen()
        }
    }

    override fun postConfirm(request: ConfirmRequest) {
        viewModelScope.launch {
            useCase.confirm(request).collectLatest {

                it.onError {
                    errorToastMessage.tryEmit(it.message.toString())
                }

                it.onSuccess {
                    confirmResponse.tryEmit(it)
                }

            }
        }
    }

    override val confirmResponse = flow<ConfirmResponse>()


    override val errorToastMessage = flow<String>()

    private val _toastMessage = MutableSharedFlow<String>(replay = 1)
    override val toastMessage: Flow<String> get() = _toastMessage




    override fun postRegister(request: RegisterRequest) {
        viewModelScope.launch {
            useCase.register(request).collectLatest { result ->
                result.onError { error ->
                    _toastMessage.emit("${error.message}")
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