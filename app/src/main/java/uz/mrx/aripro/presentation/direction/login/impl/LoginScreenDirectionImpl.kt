package uz.mrx.aripro.presentation.direction.login.impl

import uz.mrx.aripro.presentation.direction.login.LoginScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.auth.login.LoginScreenDirections
import javax.inject.Inject

class LoginScreenDirectionImpl @Inject constructor(private val navigator: Navigator) :
    LoginScreenDirection {

    override suspend fun openConfirmScreen(phoneNumber: String, code: String) {
        navigator.navigateTo(LoginScreenDirections.actionLoginScreenToConfirmScreen(phoneNumber, code))
    }


}