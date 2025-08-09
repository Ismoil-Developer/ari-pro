package uz.mrx.aripro.presentation.ui.viewmodel.profile

import kotlinx.coroutines.flow.Flow
import uz.mrx.aripro.data.remote.response.profile.ContactResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse

interface ProfilePageViewModel {

    val profileResponse:Flow<ProfileResponse>

    fun openProfileScreen()

    val getContact:Flow<ContactResponse>

    fun openLoginScreen()

}