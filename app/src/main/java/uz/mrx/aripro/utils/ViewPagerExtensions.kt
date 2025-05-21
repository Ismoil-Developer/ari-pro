package uz.mrx.aripro.utils

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

object ViewPagerExtensions {

    fun ViewPager2.addCarouselEffect(enableZoom: Boolean = true) {
        clipChildren = false
        clipToPadding = false
        offscreenPageLimit = 3
        (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((16 * Resources.getSystem().displayMetrics.density).toInt())) // Adjusted margin
        if (enableZoom) {
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = (0.80f + r * 0.20f)
            }
        }
        setPageTransformer(compositePageTransformer)
    }
}
