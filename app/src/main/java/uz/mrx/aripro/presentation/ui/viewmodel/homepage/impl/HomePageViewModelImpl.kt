package uz.mrx.aripro.presentation.ui.viewmodel.homepage.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.mrx.aripro.data.remote.response.history.OrderHistoryResponse
import uz.mrx.aripro.data.remote.response.order.AssignedResponse
import uz.mrx.aripro.data.remote.response.order.DeliveryWeeklyPriceResponse
import uz.mrx.aripro.data.remote.response.order.WorkActiveResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.domain.usecase.profile.ProfileUseCase
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.HomePageViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class HomePageViewModelImpl @Inject constructor(
    private val useCase: OrderUseCase,
    private val profileUseCase: ProfileUseCase,
    private val direction: MainScreenDirection
) : ViewModel(), HomePageViewModel {

    private val _incomingOrders = MutableSharedFlow<WebSocketOrderEvent.NewOrder>()
    override val incomingOrders: SharedFlow<WebSocketOrderEvent.NewOrder> = _incomingOrders

    private val _orderTimeouts = MutableSharedFlow<WebSocketOrderEvent.OrderTimeout>()
    override val orderTimeouts: SharedFlow<WebSocketOrderEvent.OrderTimeout> = _orderTimeouts

    private val _orderTaken = MutableSharedFlow<WebSocketOrderEvent.OrderTaken>()
    override val orderTaken: SharedFlow<WebSocketOrderEvent.OrderTaken> = _orderTaken

    override val profileResponse = flow<DeliveryHomeResponse>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            profileUseCase.getProfile().collectLatest {
                it.onSuccess {
                    withContext(Dispatchers.Main) {
                        profileResponse.tryEmit(it)
                    }
                }
                it.onError {
                    Log.e("ProfileError", it.message ?: "Unknown error")
                }
            }
        }

    }


    override fun connectWebSocket(url: String, token: String) {
        useCase.connectWebSocket(url, token)

        observeMessages()

    }

    private fun observeMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.observeMessages().collectLatest { message ->
                withContext(Dispatchers.Main) {
                    handleIncomingMessage(message)
                }
            }
        }

    }


    private suspend fun handleIncomingMessage(message: WebSocketOrderEvent) {
        when (message) {
            is WebSocketOrderEvent.NewOrder -> {
                _incomingOrders.emit(message) // Emit the NewOrder event directly
            }

            is WebSocketOrderEvent.OrderTimeout -> {
                _orderTimeouts.emit(message) // Emit the OrderTimeout event directly
            }

            is WebSocketOrderEvent.OrderTaken -> {
                _orderTaken.tryEmit(message) // Emit the OrderTaken event directly
            }

            is WebSocketOrderEvent.UnknownMessage -> {
                // Ignore or log unknown messages
            }
        }
    }


    override fun acceptOrder(orderId: Int) {
        useCase.acceptOrder(orderId)
    }

    override fun rejectOrder(orderId: Int) {
        useCase.rejectOrder(orderId)
    }

    override fun disconnectWebSocket() {
        useCase.disconnectWebSocket()
    }

    override fun openOrderDetailScreen() {
        viewModelScope.launch {
            direction.openOrderDetailScreen()
        }
    }

    override fun openOrderDeliveryScreen(id:Int) {
        viewModelScope.launch(Dispatchers.Main) {
            direction.openOrderDeliveryScreen(id)
        }

    }


    override fun postDeliveryActive() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.postDeliveryActive().collectLatest {
                it.onSuccess {
                    withContext(Dispatchers.Main) {
                        deliveryActiveResponse.tryEmit(it)
                    }

                    // status o'zgargandan so'ng profilni qayta chaqirish
                    profileUseCase.getProfile().collectLatest { result ->
                        result.onSuccess {
                            withContext(Dispatchers.Main) {
                                profileResponse.tryEmit(it)
                            }
                        }

                        result.onError {
                            Log.e("ProfileRefresh", "Error refreshing profile: ${it.message}")
                        }
                    }
                }
                it.onError {
                    Log.e("DeliveryActive", "Error toggling active status: ${it.message}")
                }
            }
        }
    }

    override val assignedResponse = flow<List<AssignedResponse>>()

    init {
        viewModelScope.launch {
            useCase.getAssignedOrder().collectLatest {
                it.onSuccess {
                    assignedResponse.tryEmit(it)
                }
                it.onError {

                }
            }
        }
    }

    override val deliveryActiveResponse = flow<WorkActiveResponse>()

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

    override val deliveryWeeklyPriceResponse = flow<DeliveryWeeklyPriceResponse>()

    init {
        viewModelScope.launch {
            useCase.getDeliveryWeeklyPrice().collectLatest {
                it.onSuccess {
                    deliveryWeeklyPriceResponse.tryEmit(it)
                }
                it.onError {

                }
            }
        }
    }

}