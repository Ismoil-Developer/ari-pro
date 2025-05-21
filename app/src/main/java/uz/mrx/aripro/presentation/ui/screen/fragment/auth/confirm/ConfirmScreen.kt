package uz.mrx.aripro.presentation.ui.screen.fragment.auth.confirm

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.databinding.ScreenConfirmBinding
import uz.mrx.aripro.presentation.ui.viewmodel.confirm.ConfirmScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.confirm.impl.ConfirmScreenViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmScreen : Fragment(R.layout.screen_confirm) {

    private val binding: ScreenConfirmBinding by viewBinding(ScreenConfirmBinding::bind)
    private val viewModel: ConfirmScreenViewModel by viewModels<ConfirmScreenViewModelImpl>()
    private var countDownTimer: CountDownTimer? = null
    private val args:ConfirmScreenArgs by navArgs()

    @Inject
    lateinit var shp: MySharedPreference
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.confirmResponse.collectLatest {
                shp.token = it.access
                Log.d("AAA", "onViewCreated: ${shp.token}")
                viewModel.openScreen()
            }
        }

        binding.resend.setOnClickListener {
            startCountdown()
        }

        val editTexts = listOf(
            binding.edt1, binding.edt2, binding.edt3,
            binding.edt4, binding.edt5
        )

        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < editTexts.size - 1) {
                        editTexts[index + 1].requestFocus() // Keyingi EditText ga o'tish
                    } else if (s.isNullOrEmpty() && index > 0) {
                        editTexts[index - 1].requestFocus() // Oldingi EditText ga qaytish
                    }
                    checkCode(editTexts)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        startCountdown()
    }

    private fun checkCode(editTexts: List<EditText>) {
        val enteredCode = editTexts.joinToString("") { it.text.toString() }

        Log.d("AAAA", "checkCode: ${args.code}")

        if (enteredCode.length == 5) {
            if (enteredCode == args.code ) {
                viewModel.postConfirm(ConfirmRequest(args.phoneNumber, enteredCode))
                editTexts.forEach { it.setBackgroundResource(R.drawable.back_password_true) }
            } else {
                editTexts.forEach { it.setBackgroundResource(R.drawable.back_password_false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startCountdown() {
        countDownTimer?.cancel() // Eski taymerni bekor qilish
        countDownTimer = object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                binding.imageRepeat.text = timeString

            }

            override fun onFinish() {
                countDownTimer = null
                Toast.makeText(requireContext(), "Vaqt tugadi", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

}