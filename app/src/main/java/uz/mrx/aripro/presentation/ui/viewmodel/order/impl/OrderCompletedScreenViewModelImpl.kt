package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.request.order.OrderFeedBackRequest
import uz.mrx.aripro.data.remote.response.order.OrderFeedBackResponse
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.direction.order.OrderCompletedScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderCompletedScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class OrderCompletedScreenViewModelImpl @Inject constructor(private val direction: OrderCompletedScreenDirection, private val useCase: OrderUseCase) : OrderCompletedScreenViewModel, ViewModel() {

    override fun openMainScreen() {
        viewModelScope.launch {
            direction.openMainScreen()
        }
    }

    override fun postFeedBack(id: Int, request: OrderFeedBackRequest) {
        viewModelScope.launch {
            useCase.postFeedBack(id, request).collectLatest {
                it.onError {

                }
                it.onSuccess {
                    feedBackResponse.tryEmit(it)
                }
            }
        }
    }

    override val feedBackResponse = flow<OrderFeedBackResponse>()

}