package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.order.CheckUploadResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.direction.order.PaymentConfirmScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.PaymentConfirmScreenViewModel
import uz.mrx.aripro.utils.flow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PaymentConfirmScreenViewModelImpl @Inject constructor(private val useCase: OrderUseCase, private val direction: PaymentConfirmScreenDirection):PaymentConfirmScreenViewModel, ViewModel(){

    override fun uploadCheck(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ){
        viewModelScope.launch {
            useCase.uploadCheck(orderId, imageFile, price).collectLatest {
                it.onError {
                    Log.e("UploadCheck", "Error: ${it.message}")
                }
                it.onSuccess {
                    uploadCheckResponse.tryEmit(it)
                }
            }
        }
    }

    override val uploadCheckResponse = flow<CheckUploadResponse>()

    override fun uploadCheckManual(
        orderId: Int,
        imageFile: Uri,
        price: Double
    ){
        viewModelScope.launch {
            useCase.uploadCheckManual(orderId, imageFile, price).collectLatest {
                it.onError {
                    Log.e("UploadCheck", "Error: ${it.message}")
                }
                it.onSuccess {
                    uploadCheckManualResponse.tryEmit(it)
                }
            }
        }
    }

    override val uploadCheckManualResponse = flow<CheckUploadResponse>()

    override fun openQrScannerFragment() {
        viewModelScope.launch {
            direction.openQrScannerFragment()
        }
    }

}