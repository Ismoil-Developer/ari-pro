package uz.mrx.aripro.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import androidx.core.view.GestureDetectorCompat

open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {
    private val gestureDetector: GestureDetectorCompat

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        gestureDetector = GestureDetectorCompat(context, GestureListener())
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - (e1?.x ?: 0f)
            val distanceY = e2.y - (e1?.y ?: 0f)
            if (abs(distanceX) > abs(distanceY) &&
                abs(distanceX) > SWIPE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                return true
            } else if (abs(distanceY) > SWIPE_THRESHOLD &&
                abs(velocityY) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceY > 0) {
                    onSwipeBottom()
                } else {
                    onSwipeTop()
                }
                return true
            }
            return false
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeTop() {}
    open fun onSwipeBottom() {}
}
