package uz.mrx.aripro.presentation.direction.order.impl

import uz.mrx.aripro.presentation.direction.order.OrderDetailScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import javax.inject.Inject

class OrderDetailScreenDirectionImpl @Inject constructor(private val navigator: Navigator):
    OrderDetailScreenDirection {


        override suspend fun openCancelScreen(id: Int) {

    }


}