package uz.mrx.aripro.presentation.ui.viewmodel.history

import androidx.camera.core.processing.SurfaceProcessorNode.In
import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.history.OrderHistoryDetailResponse

interface HistoryDetailScreenViewModel {


     fun getHistoryById(id:Int)

    val getHistoryByIdResponse:Flow<OrderHistoryDetailResponse>

}