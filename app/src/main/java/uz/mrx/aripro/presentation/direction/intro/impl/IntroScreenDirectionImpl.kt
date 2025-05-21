package uz.mrx.aripro.presentation.direction.intro.impl

import uz.mrx.aripro.presentation.direction.intro.IntroScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.intro.IntroScreenDirections
import javax.inject.Inject

class IntroScreenDirectionImpl @Inject constructor(
    private val navigator: Navigator
) : IntroScreenDirection {


    override suspend fun openLoginScreen() {
        navigator.navigateTo(IntroScreenDirections.actionIntroScreenToLoginScreen())
    }

}
