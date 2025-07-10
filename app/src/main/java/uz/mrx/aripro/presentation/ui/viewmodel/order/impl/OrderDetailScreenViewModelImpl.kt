package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.order.OrderDetailResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderDetailScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject


@HiltViewModel
class OrderDetailScreenViewModelImpl @Inject constructor(private val useCase: OrderUseCase):OrderDetailScreenViewModel, ViewModel() {
    override val orderDetailResponse = flow<OrderDetailResponse>()

    override fun getOrderDetail(id: Int) {
        viewModelScope.launch {
            useCase.getOrderDetail(id).collectLatest {

                it.onError {

                }

                it.onSuccess {
                    orderDetailResponse.tryEmit(it)
                }

            }
        }
    }

}