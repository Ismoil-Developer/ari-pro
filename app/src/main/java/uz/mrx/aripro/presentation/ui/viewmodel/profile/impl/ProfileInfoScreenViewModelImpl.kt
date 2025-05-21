package uz.mrx.aripro.presentation.ui.viewmodel.profile.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse

import uz.mrx.aripro.domain.usecase.profile.ProfileUseCase
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.profile.ProfileInfoScreenViewModel
import uz.mrx.aripro.utils.flow
import javax.inject.Inject

@HiltViewModel
class ProfileInfoScreenViewModelImpl @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val direction:MainScreenDirection,
) : ViewModel(), ProfileInfoScreenViewModel {

    override val profileResponse = flow<ProfileResponse>()

    init {
        viewModelScope.launch {
            profileUseCase.getProfilePage().collectLatest {
                it.onSuccess {
                    profileResponse.tryEmit(it)
                }
                it.onError {
                    Log.d("ProfileResponse", "ProfileResponse: ${it.message}")
                }
            }
        }
    }




}