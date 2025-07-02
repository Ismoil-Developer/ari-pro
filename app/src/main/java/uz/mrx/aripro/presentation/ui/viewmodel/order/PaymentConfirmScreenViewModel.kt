package uz.mrx.aripro.presentation.ui.viewmodel.order

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.order.CheckUploadResponse
import java.io.File

interface PaymentConfirmScreenViewModel {

    fun uploadCheck(
        orderId: Int,
        imageFile: Uri,
        price: Double
    )

    val uploadCheckResponse:Flow<CheckUploadResponse>

}