package uz.mrx.aripro.presentation.ui.viewmodel.orderpage.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.ui.viewmodel.orderpage.OrderDeliveryScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class OrderDeliveryScreenViewModelImpl @Inject constructor(private val orderUseCase: OrderUseCase):OrderDeliveryScreenViewModel,ViewModel() {
    override val orderActiveResponse = flow<OrderActiveResponse>()

    override fun postDirection(id: Int, request: DirectionRequest) {
        viewModelScope.launch {
            orderUseCase.postDirection(id,request).collectLatest {

                it.onError {

                }

                it.onSuccess {
                    responseDirection.tryEmit(it)
                }

            }
        }
    }

    override val responseDirection = flow<DirectionResponse>()

    init {
        viewModelScope.launch {
            orderUseCase.getOrderActive().collectLatest {
                it.onError {

                }
                it.onSuccess {
                    orderActiveResponse.tryEmit(it)
                }
            }
        }
    }

}