package uz.mrx.aripro.presentation.ui.viewmodel.chat.impl

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.mrx.aripro.presentation.direction.chat.ChatScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.chat.ChatScreenViewModel
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModelImpl @Inject constructor(private val direction: ChatScreenDirection):
    ChatScreenViewModel, ViewModel(){



}