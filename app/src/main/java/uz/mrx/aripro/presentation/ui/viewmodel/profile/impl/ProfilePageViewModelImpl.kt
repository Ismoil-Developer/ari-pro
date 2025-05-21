package uz.mrx.aripro.presentation.ui.viewmodel.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse
import uz.mrx.aripro.domain.usecase.profile.ProfileUseCase
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.profile.ProfilePageViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class ProfilePageViewModelImpl @Inject constructor(private val useCase: ProfileUseCase, private val direction: MainScreenDirection):ProfilePageViewModel, ViewModel() {

    override val profileResponse = flow<ProfileResponse>()

    init {
        viewModelScope.launch {
            useCase.getProfilePage().collectLatest {

                it.onMessage {

                }

                it.onSuccess {
                    profileResponse.tryEmit(it)
                }

            }
        }
    }


    override fun openProfileScreen() {
        viewModelScope.launch {
            direction.openProfileScreen()
        }
    }

}