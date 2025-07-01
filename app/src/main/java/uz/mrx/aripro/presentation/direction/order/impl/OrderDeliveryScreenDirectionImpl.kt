package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.OrderDeliveryScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.order.OrderDeliveryScreenDirections

import javax.inject.Inject

class OrderDeliveryScreenDirectionImpl @Inject constructor(private val navigator: Navigator):
    OrderDeliveryScreenDirection {

    override suspend fun openChatScreen() {
        navigator.navigateTo(OrderDeliveryScreenDirections.actionOrderDeliveryScreenToChatScreen())
    }

    override suspend fun openOrderCompletedScreen(id: Int) {
        navigator.navigateTo(OrderDeliveryScreenDirections.actionOrderDeliveryScreenToOrderCompletedScreen(id))
    }

    override suspend fun openOrderDetail(id: Int) {
        navigator.navigateTo(OrderDeliveryScreenDirections.actionOrderDeliveryScreenToOrderDetailScreen(id))
    }

    override suspend fun openOrderCancelScreen(id: Int) {
        navigator.navigateTo(OrderDeliveryScreenDirections.actionOrderDeliveryScreenToOrderCancelScreen(id))
    }

    override suspend fun openPaymentConfirmScreen() {
        navigator.navigateTo(OrderDeliveryScreenDirections.actionOrderDeliveryScreenToPaymentConfirmScreen())
    }

}