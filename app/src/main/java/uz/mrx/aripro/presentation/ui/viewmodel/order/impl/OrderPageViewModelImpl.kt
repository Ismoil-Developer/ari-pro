package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderPageViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class OrderPageViewModelImpl @Inject constructor(private val direction:MainScreenDirection, private val useCase: OrderUseCase):OrderPageViewModel, ViewModel() {

    override fun openOrderDetailScreen() {

        viewModelScope.launch {
            direction.openOrderDetailScreen()
        }

    }

    override val historyResponse = flow<List<OrderHistoryResponse>>()

    init {

        viewModelScope.launch {
            useCase.getHistory().collectLatest {
                useCase.getHistory().collectLatest {
                    it.onSuccess {

                    }
                    it.onSuccess {
                        historyResponse.tryEmit(it)
                    }
                    it.onMessage {

                    }
                }
            }
        }
    }

    override fun openHistoryDetailScreen(id: Int) {
        viewModelScope.launch {
            direction.openHistoryDetailScreen(id)
        }
    }

}