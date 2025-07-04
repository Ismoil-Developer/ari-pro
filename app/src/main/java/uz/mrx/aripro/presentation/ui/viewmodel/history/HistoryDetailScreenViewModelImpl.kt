package uz.mrx.aripro.presentation.ui.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.history.OrderHistoryDetailResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class HistoryDetailScreenViewModelImpl @Inject constructor(private val useCase: OrderUseCase):HistoryDetailScreenViewModel, ViewModel() {

    override  fun getHistoryById(id: Int) {
        viewModelScope.launch {
            useCase.getHistoryById(id).collectLatest {
                it.onSuccess {
                    getHistoryByIdResponse.tryEmit(it)
                }
                it.onError {

                }
                it.onMessage {

                }
            }
        }
    }

    override val getHistoryByIdResponse = flow<OrderHistoryDetailResponse>()

}