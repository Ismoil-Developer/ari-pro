package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.content.Intent
import android.net.Uri
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
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.databinding.PageProfileBinding
import uz.mrx.aripro.presentation.ui.dialog.ContactDialog
import uz.mrx.aripro.presentation.ui.dialog.LanguageDialog
import uz.mrx.aripro.presentation.ui.dialog.LogoutDialog
import uz.mrx.aripro.presentation.ui.viewmodel.profile.ProfilePageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.profile.impl.ProfilePageViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePage : Fragment(R.layout.page_profile) {

    private val binding: PageProfileBinding by viewBinding(PageProfileBinding::bind)
    private val viewModel: ProfilePageViewModel by viewModels<ProfilePageViewModelImpl>()

    @Inject
    lateinit var shp:MySharedPreference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.logOut.setOnClickListener {
            val dialog = LogoutDialog {

                shp.token = ""

                viewModel.openLoginScreen()

            }

            dialog.show(parentFragmentManager, "LogoutDialog")

        }


        binding.edtLanguage.setOnClickListener {
            val dialog = LanguageDialog()
            dialog.show(parentFragmentManager, "LanguageDialog")
        }

        binding.edtContactUs.setOnClickListener {
            val dialog = ContactDialog(
                onCallClick = { number ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                    startActivity(intent)
                },
                onTelegramClick = { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                },
                onChatClick = {
//                    viewModel.openChatScreen()
                }
            )

            // ViewModel orqali maʼlumot olib, dialogga yuboriladi
            lifecycleScope.launch {
                viewModel.getContact.collectLatest { response ->
                    dialog.setContactData(response.phone_number, response.telegram_link)
                }
            }

            dialog.show(parentFragmentManager, "ContactDialog")
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileResponse.collectLatest {
                binding.profileName.text = it.full_name
                binding.profileNumber.text = it.phone_number
                binding.workData.text =
                    it.work_start.substring(0, 5) + " - " + it.work_end.substring(0, 5)

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

        binding.btnEdt.setOnClickListener {
            viewModel.openProfileScreen()
        }

    }
}