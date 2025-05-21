package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.PageProfileBinding
import uz.mrx.aripro.presentation.ui.viewmodel.profile.ProfilePageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.profile.impl.ProfilePageViewModelImpl

@AndroidEntryPoint
class ProfilePage:Fragment(R.layout.page_profile) {

    private val binding:PageProfileBinding by viewBinding(PageProfileBinding::bind)
    private val viewModel:ProfilePageViewModel by viewModels<ProfilePageViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileResponse.collectLatest {
                binding.profileName.text = it.full_name
                binding.profileNumber.text = it.phone_number
                binding.workData.text = it.work_start.substring(0, 5) +" - "+ it.work_end.substring(0,5)
            }
        }

        binding.btnEdt.setOnClickListener {
            viewModel.openProfileScreen()
        }

    }
}