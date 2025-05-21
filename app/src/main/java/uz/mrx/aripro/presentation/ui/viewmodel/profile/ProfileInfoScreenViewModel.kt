package uz.mrx.aripro.presentation.ui.viewmodel.profile

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse

interface ProfileInfoScreenViewModel {

    val profileResponse: Flow<ProfileResponse>


}