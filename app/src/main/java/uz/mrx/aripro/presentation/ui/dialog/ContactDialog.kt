package uz.mrx.aripro.presentation.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import uz.mrx.aripro.databinding.DialogContactBinding

class ContactDialog(
    private val onCallClick: (String) -> Unit,
    private val onTelegramClick: (String) -> Unit,
    private val onChatClick: () -> Unit
) : DialogFragment() {

    private var _binding: DialogContactBinding? = null
    private val binding get() = _binding!!

    private var phoneNumber: String? = null
    private var telegramLink: String? = null

    fun setContactData(phone: String?, telegram: String?) {
        phoneNumber = phone
        telegramLink = telegram
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.escImg.setOnClickListener { dismiss() }

        binding.contactCall.setOnClickListener {
            phoneNumber?.let { number ->
                onCallClick(number)
                dismiss()
            } ?: Toast.makeText(requireContext(), "Telefon raqami mavjud emas", Toast.LENGTH_SHORT).show()
        }

        binding.chatCall.setOnClickListener {
            onChatClick()
            dismiss()
        }

        binding.contactTelegram.setOnClickListener {
            telegramLink?.let { link ->
                onTelegramClick(link)
                dismiss()
            } ?: Toast.makeText(requireContext(), "Telegram havolasi mavjud emas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.setWindowAnimations(android.R.style.Animation_Dialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
