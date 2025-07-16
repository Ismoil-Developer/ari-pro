package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenPaymentConfirmBinding
import uz.mrx.aripro.presentation.ui.viewmodel.order.PaymentConfirmScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.PaymentConfirmScreenViewModelImpl
import uz.mrx.aripro.utils.toast
import java.io.File

@AndroidEntryPoint
class PaymentConfirmScreen : Fragment(R.layout.screen_payment_confirm) {

    private val binding by viewBinding(ScreenPaymentConfirmBinding::bind)
    private val viewModel: PaymentConfirmScreenViewModel by viewModels<PaymentConfirmScreenViewModelImpl>()
    private val args: PaymentConfirmScreenArgs by navArgs()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPermissionRequest()
        setupTakePictureLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.repeatBtn.setOnClickListener {
            val priceInput = binding.edtCost.text.toString().trim()
            val price = priceInput.toDoubleOrNull()

            if (price == null) {
                toast("Narx noto'g'ri formatda kiritilgan")
                return@setOnClickListener
            }

            if (photoUri == null) {
                toast("Iltimos, rasm yuklang")
                return@setOnClickListener
            }

            viewModel.uploadCheckManual(args.id, photoUri!!, price)
        }



        setupViews()
        observeViewModel()
    }

    private fun setupViews() = with(binding) {
        icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        containerImg.setOnClickListener {
            viewModel.openQrScannerFragment(args.id)
        }

        camera.setOnClickListener {
            val priceInput = edtCost.text.toString().trim()
            if (priceInput.isEmpty()) {
                toast("Iltimos, narxni kiriting")
                return@setOnClickListener
            }

            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                arrayOf(Manifest.permission.CAMERA)
            }
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uploadCheckManualResponse.collectLatest { response ->
                Log.d("RRRRR", "observeViewModel: ${response.order}")
                viewModel.openPaymentScreenToDeliveryScreen(response.order)
            }
        }
    }

    private fun setupPermissionRequest() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                openCamera()
            } else {
                toast("Kamera uchun ruxsat berilmadi")
            }
        }
    }

    private fun setupTakePictureLauncher() {
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    binding.imageQr.setImageURI(uri)
                } ?: toast("Rasm topilmadi")
            } else {
                toast("Rasm olinmadi")
            }
        }
    }


    private fun openCamera() {
        val imageFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "check_${System.currentTimeMillis()}.jpg"
        )

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

        photoUri = uri
        takePictureLauncher.launch(uri)
    }

}
