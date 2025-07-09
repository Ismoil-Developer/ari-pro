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
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenPaymentConfirmBinding
import uz.mrx.aripro.presentation.ui.viewmodel.order.PaymentConfirmScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.order.impl.PaymentConfirmScreenViewModelImpl
import uz.mrx.aripro.utils.toast
import java.io.File

@AndroidEntryPoint
class PaymentConfirmScreen : Fragment(R.layout.screen_payment_confirm) {

    private val binding: ScreenPaymentConfirmBinding by viewBinding(ScreenPaymentConfirmBinding::bind)
    private val viewModel: PaymentConfirmScreenViewModel by viewModels<PaymentConfirmScreenViewModelImpl>()
    private val args:PaymentConfirmScreenArgs by navArgs()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    private val price = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kamera permission
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                openCamera()
            } else {
                toast("Kamera uchun ruxsat berilmadi")
            }
        }

        // Kamera natijasini olish
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    binding.imageQr.setImageURI(uri)
                    viewModel.uploadCheckManual(args.id, uri, price)
                } ?: toast("Rasm URI topilmadi")
            } else {
                toast("Rasm olinmadi")
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.containerImg.setOnClickListener {
            viewModel.openQrScannerFragment(args.id)
        }


        binding.camera.setOnClickListener {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.CAMERA)
            }
            requestPermissionLauncher.launch(permissions)
        }

        // Upload natijasi
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uploadCheckResponse.collectLatest { response ->
                toast("Yuklandi: ${response.qrUrl}")
                Log.d("UploadResult", "QR URL: ${response.image}")
            }
        }
    }

    private fun openCamera() {
        // Fayl nomi va joylashuvini belgilaymiz
        val imageFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "check_${System.currentTimeMillis()}.jpg"
        )

        // Uri ni yaratamiz (FileProvider orqali)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

        // Uri ni saqlab qo'yamiz (keyinchalik rasmni ko'rsatish yoki yuklash uchun)
        photoUri = uri

        // Kamera ilovasini ishga tushiramiz
        takePictureLauncher.launch(uri)
    }

}