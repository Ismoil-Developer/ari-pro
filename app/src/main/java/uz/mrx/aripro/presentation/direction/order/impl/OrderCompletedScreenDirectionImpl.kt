package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.OrderCompletedScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.order.OrderCompletedScreenDirections

import javax.inject.Inject

class OrderCompletedScreenDirectionImpl @Inject constructor(private val navigator: Navigator) :
    OrderCompletedScreenDirection {

        override suspend fun openMainScreen() {
        navigator.navigateTo(OrderCompletedScreenDirections.actionOrderCompletedScreenToScreenMain())

    }

}