package uz.mrx.aripro.presentation.ui.viewmodel.confirm

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse

interface ConfirmScreenViewModel {

    fun openScreen()

    fun postConfirm(request: ConfirmRequest)

    val confirmResponse: Flow<ConfirmResponse>


}