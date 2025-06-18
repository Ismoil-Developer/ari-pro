package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import uz.mrx.aripro.data.remote.request.register.DirectionRequest
import uz.mrx.aripro.data.remote.response.order.DirectionResponse
import uz.mrx.aripro.data.remote.response.order.OrderActiveResponse
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.direction.order.OrderDeliveryScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderDeliveryScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class OrderDeliveryScreenViewModelImpl @Inject constructor(private val orderUseCase: OrderUseCase, private val direction: OrderDeliveryScreenDirection):OrderDeliveryScreenViewModel,ViewModel() {
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

    override val orderActiveToken = flow<OrderActiveResponse>()
    override fun openOrderCompletedScreen(id: Int) {
        viewModelScope.launch {
            direction.openOrderCompletedScreen(id)
        }
    }

    override fun openOrderDetailScreen(id: Int) {
        viewModelScope.launch {
            direction.openOrderDetail(id)
        }    }

    override fun orderCancelScreen(id: Int) {
        viewModelScope.launch {
            direction.openOrderCancelScreen(id)
        }    }

    override fun openChatScreen() {
        viewModelScope.launch {
            direction.openChatScreen()
        }
    }

    override fun startSendingLocation(locationProvider: suspend () -> Pair<Double, Double>) {
        orderUseCase.startLocationUpdates(locationProvider)
    }

    override fun stopSendingLocation() = orderUseCase.stopLocationUpdates()

    override fun connectWebSocket(url: String, token: String) {

        orderUseCase   .connectWebSocket(url, token)

    }

    override fun disconnectWebSocket() {
        orderUseCase.disconnectWebSocket()
    }

    init {
        viewModelScope.launch {
            orderUseCase.getOrderActiveToken().collectLatest {
                it.onError {

                }
                it.onSuccess {
                    orderActiveToken.tryEmit(it)
                }
            }
        }
    }

    override fun getOrderActive(id: Int) {
        viewModelScope.launch {
            orderUseCase.getOrderActive(id).collectLatest {
                it.onError {

                }
                it.onSuccess {
                    orderActiveResponse.tryEmit(it)
                }
            }
        }
    }

}