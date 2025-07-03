package uz.mrx.aripro.presentation.ui.screen.fragment.order

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenQrScannerBinding
import uz.mrx.aripro.utils.toast
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrScannerFragment : Fragment(R.layout.screen_qr_scanner) {

    private val binding by viewBinding(ScreenQrScannerBinding::bind)
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var isScanned = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        startCamera()

    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !isScanned) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        val scanner = BarcodeScanning.getClient()

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    if (barcode.valueType == Barcode.TYPE_URL || barcode.rawValue?.contains(
                                            "soliq"
                                        ) == true
                                    ) {
                                        barcode.rawValue?.let {
                                            isScanned = true
                                            onQrCodeScanned(it)
                                            updateFrameToGreen()
                                            imageProxy.close()
                                        }
                                        return@addOnSuccessListener
                                    }
                                }
                                imageProxy.close()
                            }
                            .addOnFailureListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                toast("Kamera ishga tushmadi")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun onQrCodeScanned(qrText: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            toast("QR topildi")
            Log.d("QrScanner", "QR: $qrText")
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(qrText)
            }
            startActivity(intent)
        }
    }

    private fun updateFrameToGreen() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.qrFrameView.setBackgroundResource(R.drawable.qr_frame_green)
        }
    }

}
