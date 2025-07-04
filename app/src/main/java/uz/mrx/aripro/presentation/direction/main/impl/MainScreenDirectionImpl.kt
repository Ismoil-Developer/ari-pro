package uz.mrx.aripro.presentation.direction.main.impl

import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.main.MainScreenDirections
import javax.inject.Inject

class MainScreenDirectionImpl @Inject constructor(private val navigator: Navigator) :
    MainScreenDirection {

    override suspend fun openOrderDetailScreen() {
        navigator.navigateTo(MainScreenDirections.actionScreenMainToOrderDetailScreen())
    }

    override suspend fun openProfileScreen() {
        navigator.navigateTo(MainScreenDirections.actionScreenMainToProfileInfoScreen())
    }

    override suspend fun openOrderDeliveryScreen(id:Int) {
        navigator.navigateTo(MainScreenDirections.actionScreenMainToOrderDeliveryScreen(id))
    }

    override suspend fun openHistoryDetailScreen(id: Int) {
        navigator.navigateTo(MainScreenDirections.actionScreenMainToHistoryDetailScreen(id))
    }

}