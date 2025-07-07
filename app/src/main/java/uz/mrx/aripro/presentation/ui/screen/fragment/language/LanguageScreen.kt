package uz.mrx.aripro.presentation.ui.screen.fragment.language

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.databinding.ScreenLanguageBinding
import uz.mrx.aripro.presentation.ui.viewmodel.language.LanguageScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.language.impl.LanguageScreenViewModelImpl
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class LanguageScreen : Fragment(R.layout.screen_language) {

    private val binding: ScreenLanguageBinding by viewBinding(ScreenLanguageBinding::bind)
    private val viewModel: LanguageScreenViewModel by viewModels<LanguageScreenViewModelImpl>()

    @Inject
    lateinit var sharedPreference: MySharedPreference
    private var selectedLanguage: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbUz -> {
                    selectedLanguage = "uz"
                }

                R.id.rbRus -> {
                    selectedLanguage = "ru"

                }


            }

            setLocate(selectedLanguage)
            updateSelection()

        }

        binding.btnContinueLn.setOnClickListener {
            if (selectedLanguage.isNotEmpty()) {
                viewModel.openIntroScreen()
            } else {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateSelection() {

        binding.rbUz.apply {
            setBackgroundResource(if (selectedLanguage == "uz") R.drawable.shape_selected else R.drawable.shape_unselected)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_flag_uz,
                0,
                if (selectedLanguage == "uz") R.drawable.ic_selected_lan else R.drawable.ic_unselected_lan,
                0
            )
            updateUi()

        }

        binding.rbRus.apply {
            setBackgroundResource(if (selectedLanguage == "ru") R.drawable.shape_selected else R.drawable.shape_unselected)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_flag_ru,
                0,
                if (selectedLanguage == "ru") R.drawable.ic_selected_lan else R.drawable.ic_unselected_lan,
                0
            )
            updateUi()
        }


    }

    private fun updateUi() {
        binding.chooseLanguage.text = getString(R.string.tilni_tanlang)
        binding.changeLanguage.text = getString(R.string.tilni_keyinchalik_o_zgartirishingiz_ham_mumkun)
        binding.btnContinueLn.text = getString(R.string.davom_etish)
    }

    @SuppressLint("ResourceAsColor")
    private fun setLocate(language: String) {
        val resources: Resources = resources
        val metric: DisplayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(language))
        resources.updateConfiguration(configuration, metric)
        sharedPreference.language = language
    }

}