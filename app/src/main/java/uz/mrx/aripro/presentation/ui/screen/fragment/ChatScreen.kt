package uz.mrx.aripro.presentation.ui.screen.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenChatBinding
import uz.mrx.aripro.presentation.ui.viewmodel.chat.ChatScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.chat.impl.ChatScreenViewModelImpl

@AndroidEntryPoint
class ChatScreen:Fragment(R.layout.screen_chat) {

    private val binding: ScreenChatBinding by viewBinding(ScreenChatBinding::bind)
    private val viewModel: ChatScreenViewModel by viewModels<ChatScreenViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}