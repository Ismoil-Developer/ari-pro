package uz.mrx.aripro.presentation.direction.confirm.impl

import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.auth.confirm.ConfirmScreenDirections
import uz.mrx.aripro.presentation.direction.confirm.ConfirmScreenDirection
import javax.inject.Inject

class ConfirmScreenDirectionImpl @Inject constructor(
    private val navigator: Navigator
) : ConfirmScreenDirection {

    override suspend fun openMainScreen() {
        navigator.navigateTo(ConfirmScreenDirections.actionConfirmScreenToScreenMain())
    }

}