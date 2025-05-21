package uz.mrx.aripro.presentation.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import uz.mrx.aripro.databinding.DialogTimeBinding

class OrderTimeDialog(
    context: Context,
    private val orderDesc: String? = null,
    private val orderPrice: String? = null,

    private val onAcceptClickListener: () -> Unit,
    private val onSkipClickListener: () -> Unit
) : Dialog(context) {

    private val alertDialog: AlertDialog
    private val binding: DialogTimeBinding
    private var progressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null

    init {

        val builder = AlertDialog.Builder(context)
        binding = DialogTimeBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = alertDialog.window
        window?.setGravity(Gravity.TOP)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        binding.edtOrder.text = orderDesc ?: ""
        binding.textViewTask.text = orderPrice ?: ""

        binding.mcvAccept.setOnClickListener {
            onAcceptClickListener()
            dismissDialog()
        }

        binding.mcvSkip.setOnClickListener {
            onSkipClickListener()
            dismissDialog()
        }

        progressBar = binding.circularProgressBar
        startProgressBar()

        binding.buttonDetails.setOnClickListener {
            // View'larni ko‘rsatish
            binding.edtOrder.visibility = View.VISIBLE
            binding.line.visibility = View.VISIBLE
            binding.listTitle.visibility = View.VISIBLE
            binding.imageList.visibility = View.VISIBLE
            // Tugmani yashirish
            binding.buttonDetails.visibility = View.GONE
        }
    }


    override fun show() {
        alertDialog.show()
    }

    override fun dismiss() {
        dismissDialog()
    }

    private fun dismissDialog() {
        countDownTimer?.cancel()
        alertDialog.dismiss()
    }

    private fun startProgressBar() {
        val maxTime = 20 * 1000L
        progressBar.max = 20

        countDownTimer = object : CountDownTimer(maxTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                progressBar.progress = 20 - secondsLeft
                binding.timerText.text = secondsLeft.toString()
            }

            override fun onFinish() {
                progressBar.progress = 20
                binding.timerText.text = "0"
                dismissDialog()
            }
        }

        countDownTimer?.start()

    }

}
