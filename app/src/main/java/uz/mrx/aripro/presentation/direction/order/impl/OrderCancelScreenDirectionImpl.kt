package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.OrderCancelScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.order.OrderCancelScreenDirections
import javax.inject.Inject

class OrderCancelScreenDirectionImpl @Inject constructor(private val navigator: Navigator):
    OrderCancelScreenDirection {

        override suspend fun openMainScreen() {
        navigator.navigateTo(OrderCancelScreenDirections.actionOrderCancelScreenToScreenMain())
    }

}