package uz.mrx.aripro.presentation.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import uz.mrx.aripro.databinding.DialogOrderCancelledBinding

class CancelDialog(
    context: Context,
    private val onContinueClick: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogOrderCancelledBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogOrderCancelledBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Transparent background
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Fullscreen
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        setCancelable(true)

        binding.btnContinueLn.setOnClickListener {
            onContinueClick.invoke()
            dismiss()
        }

        binding.escImg.setOnClickListener {
            dismiss()
        }
    }

}
