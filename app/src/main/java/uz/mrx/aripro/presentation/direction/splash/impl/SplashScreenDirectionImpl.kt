package uz.mrx.aripro.presentation.direction.splash.impl

import uz.mrx.aripro.presentation.direction.splash.SplashScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.splash.SplashScreenDirections
import javax.inject.Inject

class SplashScreenDirectionImpl @Inject constructor(
    private val navigator: Navigator
) : SplashScreenDirection {

    override suspend fun openLanguageScreen() =
        navigator.navigateTo(SplashScreenDirections.actionSplashScreenToLanguageScreen())

    override suspend fun openMainScreen() {
        navigator.navigateTo(SplashScreenDirections.actionSplashScreenToScreenMain())
    }

}