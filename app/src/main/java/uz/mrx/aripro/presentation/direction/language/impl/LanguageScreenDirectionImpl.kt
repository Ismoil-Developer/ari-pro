package uz.mrx.aripro.presentation.direction.language.impl

import uz.mrx.aripro.presentation.direction.language.LanguageScreenDirection
import uz.mrx.aripro.presentation.navigation.Navigator
import uz.mrx.aripro.presentation.ui.screen.fragment.language.LanguageScreenDirections
import javax.inject.Inject

class LanguageScreenDirectionImpl @Inject constructor(
    private val navigator: Navigator
) : LanguageScreenDirection {

    override suspend fun openIntroScreen() {
        navigator.navigateTo(LanguageScreenDirections.actionLanguageScreenToIntroScreen())
    }

}