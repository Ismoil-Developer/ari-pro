package uz.mrx.aripro.presentation.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import uz.mrx.aripro.R
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
    private var mediaPlayer: MediaPlayer? = null

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

        // Ma'lumotlarni set qilish
        binding.edtOrder.text = orderDesc ?: ""
        binding.textViewTask.text = orderPrice ?: ""

        // Tugmalar
        binding.mcvAccept.setOnClickListener {
            stopSound()
            onAcceptClickListener()
            dismissDialog()
        }

        binding.mcvSkip.setOnClickListener {
            stopSound()
            onSkipClickListener()
            dismissDialog()
        }

        progressBar = binding.circularProgressBar
        startProgressBar()

        // Batafsil tugmasi
        binding.buttonDetails.setOnClickListener {
            binding.edtOrder.visibility = View.VISIBLE
            binding.line.visibility = View.VISIBLE
            binding.listTitle.visibility = View.VISIBLE
            binding.imageList.visibility = View.VISIBLE
            binding.buttonDetails.visibility = View.GONE
        }
    }

    override fun show() {
        alertDialog.show()
        playSound() // ✅ Dialog ochilganda musiqa chaladi
    }

    override fun dismiss() {
        stopSound()
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
                stopSound() // ✅ Vaqt tugaganda musiqa o‘chadi
                dismissDialog()
            }
        }
        countDownTimer?.start()
    }

    // ✅ Musiqa chalish funksiyasi
    private fun playSound() {
        mediaPlayer = MediaPlayer.create(context, R.raw.sound) // raw/order_sound.mp3
        mediaPlayer?.isLooping = true // Takroriy chalish
        mediaPlayer?.start()
    }

    // ✅ Musiqa to‘xtatish va resurslarni bo‘shatish
    private fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}