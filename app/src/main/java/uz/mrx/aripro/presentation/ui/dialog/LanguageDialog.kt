package uz.mrx.aripro.presentation.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.databinding.DialogLanguageBinding
import javax.inject.Inject

@AndroidEntryPoint
class LanguageDialog : DialogFragment() {

    private lateinit var binding: DialogLanguageBinding

    @Inject
    lateinit var sharedPreference: MySharedPreference

    private var selectedLanguage: String = "uz" // default

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLanguageBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        selectedLanguage = sharedPreference.language ?: "uz"

        setInitialSelection()

        binding.rbUz.setOnClickListener {
            selectedLanguage = "uz"
            updateLanguageSelectionUI()
        }

        binding.rbRus.setOnClickListener {
            selectedLanguage = "ru"
            updateLanguageSelectionUI()
        }

        binding.save.setOnClickListener {
            changeLanguage(selectedLanguage)
        }

        binding.no.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    private fun setInitialSelection() {
        when (selectedLanguage) {
            "uz" -> binding.rbUz.isChecked = true
            "ru" -> binding.rbRus.isChecked = true
        }
        updateLanguageSelectionUI()
    }

    private fun updateLanguageSelectionUI() {

        // O'zbek
        binding.rbUz.setBackgroundResource(if (selectedLanguage == "uz") R.drawable.shape_selected else R.drawable.shape_unselected)

        binding.rbUz.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_flag_uz, 0,
            if (selectedLanguage == "uz") R.drawable.ic_selected_lan else R.drawable.ic_unselected_lan, 0
        )

        // Rus
        binding.rbRus.setBackgroundResource(if (selectedLanguage == "ru") R.drawable.shape_selected else R.drawable.shape_unselected)

        binding.rbRus.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_flag_ru, 0,
            if (selectedLanguage == "ru") R.drawable.ic_selected_lan else R.drawable.ic_unselected_lan, 0
        )

    }

    private fun changeLanguage(languageCode: String) {
        sharedPreference.language = languageCode
        Lingver.getInstance().setLocale(requireContext(), languageCode)
        Toast.makeText(requireContext(), "Til o'zgartirildi", Toast.LENGTH_SHORT).show()
        dismiss()
        requireActivity().recreate()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.setGravity(Gravity.CENTER)
            window.setWindowAnimations(android.R.style.Animation_Dialog)
        }
    }

}
