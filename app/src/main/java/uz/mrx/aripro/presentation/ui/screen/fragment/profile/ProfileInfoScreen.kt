package uz.mrx.aripro.presentation.ui.screen.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenProfileInfoBinding
import uz.mrx.aripro.presentation.ui.viewmodel.profile.ProfileInfoScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.profile.impl.ProfileInfoScreenViewModelImpl

@AndroidEntryPoint
class ProfileInfoScreen:Fragment(R.layout.screen_profile_info) {

    private val binding:ScreenProfileInfoBinding by viewBinding(ScreenProfileInfoBinding::bind)
    private val viewModel:ProfileInfoScreenViewModel by viewModels<ProfileInfoScreenViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileResponse.collectLatest {
                binding.txtId.text = it.deliver_id
                binding.balanceTxt.text = it.balance
                binding.timeTxt.text = it.work_start.substring(0, 5) +" - "+ it.work_end.substring(0,5)
                binding.txtName.text = it.full_name
                binding.txtNumber.text = it.phone_number

                it.avatar?.let { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .apply(
                            RequestOptions().skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        )
                        .into(binding.profileImg)
                }

            }
        }

    }
}