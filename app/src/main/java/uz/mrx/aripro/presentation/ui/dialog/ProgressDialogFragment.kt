package uz.mrx.aripro.presentation.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.DialogFragment
import uz.mrx.aripro.databinding.DialogProgressBinding

class ProgressDialogFragment(private val maxProgress: Int, private val onComplete: () -> Unit) :
    DialogFragment() {

    private lateinit var binding: DialogProgressBinding
    private val handler = Handler(Looper.getMainLooper())
    private var progressStatus = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        binding = DialogProgressBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        startProgress()

        return dialog
    }

    private fun startProgress() {
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.max = maxProgress
        handler.post(object : Runnable {
            override fun run() {
                if (progressStatus < maxProgress) {
                    progressStatus += 1
                    binding.progressBar.progress = progressStatus
                    handler.postDelayed(this, 10)
                } else {
                    onComplete()
                    dismiss()
                }
            }
        })
    }
}
