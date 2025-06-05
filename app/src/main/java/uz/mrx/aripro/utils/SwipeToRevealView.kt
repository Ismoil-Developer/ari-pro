package uz.mrx.aripro.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import uz.mrx.aripro.R

class SwipeToRevealView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var swipeText: String = "Yetib keldim"
    private lateinit var swipeButton: View
    private lateinit var swipeLabel: TextView
    private var onSwipeListener: (() -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_swipe_to_reveal, this, true)
        swipeButton = findViewById(R.id.swipeButton)
        swipeLabel = findViewById(R.id.swipeLabel)

        swipeLabel.text = swipeText

        swipeButton.setOnTouchListener(object : OnTouchListener {
            var dX = 0f
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX + dX
                        if (newX >= 0 && newX <= width - view.width) {
                            view.x = newX
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        if (view.x >= width * 0.6) {
                            swipeLabel.text = "✔ Yetib keldim"
                            view.animate().x(width.toFloat()).withEndAction {
                                onSwipeListener?.invoke()
                                // Optional: hide the view
                                visibility = View.GONE
                            }.start()
                        } else {
                            view.animate().x(0f).start()
                        }
                    }
                }
                return true
            }
        })
    }

    fun setText(text: String) {
        swipeText = text
        swipeLabel.text = text
    }

    fun setOnSwipeListener(listener: () -> Unit) {
        onSwipeListener = listener
    }

    fun reset() {
        swipeButton.x = 0f
        swipeLabel.text = swipeText
        visibility = View.VISIBLE
    }


}
