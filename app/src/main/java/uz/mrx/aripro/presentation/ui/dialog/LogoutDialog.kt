package uz.mrx.aripro.presentation.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import uz.mrx.aripro.databinding.DialogLogoutBinding

class LogoutDialog(
    private val onLogoutConfirmed: () -> Unit
) : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.yes.setOnClickListener {
            onLogoutConfirmed.invoke()
            dialog.dismiss()
        }

        binding.no.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
