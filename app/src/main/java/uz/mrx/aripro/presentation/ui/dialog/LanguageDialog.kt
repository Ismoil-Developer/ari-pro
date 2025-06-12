package uz.mrx.aripro.presentation.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import uz.mrx.aripro.databinding.DialogLanguageBinding

class LanguageDialog : DialogFragment() {

    private lateinit var binding: DialogLanguageBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLanguageBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        // Dialog yopish tugmalari
        binding.yes.setOnClickListener {
            dismiss()
        }

        binding.no.setOnClickListener {
            dismiss()
        }

        // Tilni tanlash bo'yicha ishlov berish
        binding.rbUz.setOnClickListener {
            Toast.makeText(requireContext(), "O'zbek tili tanlandi", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.rbRus.setOnClickListener {
            Toast.makeText(requireContext(), "Русский язык выбран", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            // Dialogni ekranning o‘rtasiga joylash
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent) // Fonni shaffof qilish
            window.setGravity(Gravity.CENTER) // Dialogni ekranning markaziga qo‘yish
            window.setWindowAnimations(android.R.style.Animation_Dialog) // Animatsiya (ixtiyoriy)
        }
    }
}
