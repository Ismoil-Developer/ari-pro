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
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.databinding.DialogLanguageBinding
import javax.inject.Inject

@AndroidEntryPoint
class LanguageDialog : DialogFragment() {

    private lateinit var binding: DialogLanguageBinding

    @Inject
    lateinit var sharedPreference: MySharedPreference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLanguageBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        // Yopish tugmalari
        binding.yes.setOnClickListener { dismiss() }
        binding.no.setOnClickListener { dismiss() }

        // O'zbek tilini tanlash
        binding.rbUz.setOnClickListener {
            changeLanguage("uz")
        }

        // Rus tilini tanlash
        binding.rbRus.setOnClickListener {
            changeLanguage("ru")
        }

        return builder.create()
    }

    private fun changeLanguage(languageCode: String) {
        sharedPreference.language = languageCode // sharedPreferenceda saqlash
        Lingver.getInstance().setLocale(requireContext(), languageCode) // tilni o'zgartirish
        Toast.makeText(requireContext(), "Til o'zgartirildi", Toast.LENGTH_SHORT).show()
        dismiss()
        requireActivity().recreate() // Aktivitetni qayta yuklab, tilni amalda ko‘rsatish
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
