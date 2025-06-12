package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.direction.order.OrderCancelScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderCancelViewModel
import uz.mrx.aripro.data.remote.request.order.OrderCancelRequest
import uz.mrx.aripro.data.remote.response.order.OrderCancelResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class OrderCancelViewModelImpl  @Inject constructor(private val useCase: OrderUseCase, private val direction: OrderCancelScreenDirection):
    OrderCancelViewModel, ViewModel(){


    override fun cancelOrder(id: Int, request: OrderCancelRequest) {
        viewModelScope.launch {
            useCase.cancelOrder(id, request).collectLatest {
                it.onError {

                }
                it.onSuccess {
                    cancelResponse.tryEmit(it)
                }
            }
        }
    }

    override val cancelResponse = flow<OrderCancelResponse>()

    override fun openMainScreen() {
        viewModelScope.launch {
            direction.openMainScreen()
        }
    }

}