package uz.mrx.aripro.presentation.ui.viewmodel.order

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse

interface OrderPageViewModel {

    fun openOrderDetailScreen()

    val historyResponse: Flow<List<OrderHistoryResponse>>

    fun openHistoryDetailScreen(id:Int)

}