package uz.mrx.aripro.presentation.ui.screen.fragment.auth.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.databinding.ScreenLoginBinding
import uz.mrx.aripro.presentation.ui.viewmodel.login.LoginScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.login.impl.LoginScreenViewModelImpl
import uz.mrx.aripro.utils.OnSwipeTouchListener

@AndroidEntryPoint
class LoginScreen : Fragment(R.layout.screen_login) {

    private val binding: ScreenLoginBinding by viewBinding(ScreenLoginBinding::bind)

    private val viewModel: LoginScreenViewModel by viewModels<LoginScreenViewModelImpl>()

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.container.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {

            override fun onSwipeTop() {
                binding.container.animate().translationY(-200f).setDuration(100).start()
            }

            override fun onSwipeBottom() {
                binding.container.animate().translationY(50f).setDuration(100).start()
            }

        })



        binding.btnContinue.setSafeOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Iltimos, telefon raqamingizni kiriting", Toast.LENGTH_SHORT).show()
                return@setSafeOnClickListener
            }

            if (!phoneNumber.startsWith("+998") || phoneNumber.length != 13) {
                Toast.makeText(requireContext(), "Raqam formati xato. Namuna: +998991234567", Toast.LENGTH_SHORT).show()
                return@setSafeOnClickListener
            }

            // Hamma shartlar to'g'ri bo'lsa:
            viewModel.postRegister(RegisterRequest(phoneNumber))

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.registerResponse.collectLatest { message ->
                        if (message.detail == "Kod yuborildi.") {
                            viewModel.openConfirmScreen(phoneNumber, "")
                        } else {
                            Toast.makeText(requireContext(), "Xatolik yuz berdi", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.btnContinue.isEnabled = !isLoading
                    binding.btnContinue.alpha = if (isLoading) 0.5f else 1f
                    binding.btnContinue.text = if (isLoading) "Yuklanmoqda..." else "Davom etish"
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isLoading.collectLatest { isLoading ->
                        binding.btnContinue.setLoading(isLoading, "Yuklanmoqda...")
                    }
                }
            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collectLatest { message ->

                }
            }
        }

    }

    private var lastClickTime = 0L

    fun View.setSafeOnClickListener(interval: Long = 1000L, onSafeClick: (View) -> Unit) {
        setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > interval) {
                lastClickTime = currentTime
                onSafeClick(it)
            }
        }
    }

    fun AppCompatButton.setLoading(isLoading: Boolean, loadingText: String = "Yuklanmoqda...") {
        isEnabled = !isLoading
        text = if (isLoading) loadingText else "Davom etish"
        alpha = if (isLoading) 0.6f else 1f
    }

}