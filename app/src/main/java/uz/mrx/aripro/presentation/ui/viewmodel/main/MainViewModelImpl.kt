package uz.mrx.aripro.presentation.ui.viewmodel.main.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.presentation.ui.viewmodel.main.MainViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val useCase: OrderUseCase
) : ViewModel(), MainViewModel {

    private val _incomingOrders = MutableSharedFlow<WebSocketOrderEvent.NewOrder>()
    override val incomingOrders: SharedFlow<WebSocketOrderEvent.NewOrder> = _incomingOrders

    private val _orderTimeouts = MutableSharedFlow<WebSocketOrderEvent.OrderTimeout>()
    override val orderTimeouts: SharedFlow<WebSocketOrderEvent.OrderTimeout> = _orderTimeouts

    private val _orderTaken = MutableSharedFlow<WebSocketOrderEvent.OrderTaken>()
    override val orderTaken: SharedFlow<WebSocketOrderEvent.OrderTaken> = _orderTaken


    override fun connectWebSocket(url: String, token: String) {
        useCase.connectWebSocket(url, token)

        observeMessages()

    }


    fun observeMessages() {
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

}
