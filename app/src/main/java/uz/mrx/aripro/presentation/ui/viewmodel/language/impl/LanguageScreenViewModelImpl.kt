package uz.mrx.aripro.presentation.ui.viewmodel.language.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.direction.language.LanguageScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.language.LanguageScreenViewModel
import javax.inject.Inject


@HiltViewModel
class LanguageScreenViewModelImpl @Inject constructor(
    private val direction: LanguageScreenDirection
) : LanguageScreenViewModel, ViewModel() {

    override fun openIntroScreen() {
        viewModelScope.launch {
            direction.openIntroScreen()
        }
    }

}