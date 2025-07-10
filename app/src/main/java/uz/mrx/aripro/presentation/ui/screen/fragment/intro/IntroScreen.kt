package uz.mrx.aripro.presentation.ui.screen.fragment.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.model.IntroData
import uz.mrx.aripro.databinding.ScreenIntroBinding
import uz.mrx.aripro.presentation.adapter.CarouselAdapter
import uz.mrx.aripro.presentation.ui.viewmodel.intro.IntroScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.intro.impl.IntroScreenViewModelImpl
import uz.mrx.aripro.utils.ViewPagerExtensions.addCarouselEffect


@AndroidEntryPoint
class IntroScreen : Fragment(R.layout.screen_intro) {

    private val binding: ScreenIntroBinding by viewBinding(ScreenIntroBinding::bind)
    private val viewModel: IntroScreenViewModel by viewModels<IntroScreenViewModelImpl>()
    private val carouselAdapter = CarouselAdapter()
    lateinit var list: ArrayList<IntroData>

    private val handler = Handler(Looper.getMainLooper())
    private val slideRunnable = Runnable {
        binding.viewPager.currentItem =
            (binding.viewPager.currentItem + 1) % carouselAdapter.itemCount
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = ArrayList()

        loadData()


        carouselAdapter.submitList(list)

        binding.viewPager.apply {
            adapter = carouselAdapter
            addCarouselEffect()
        }

        startAutoSlide()

        binding.btnLogin.setOnClickListener {
            viewModel.openLogin()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                restartAutoSlide()
            }
        })
    }

    private fun startAutoSlide() {
        handler.postDelayed(slideRunnable, 3000)
    }

    private fun stopAutoSlide() {
        handler.removeCallbacks(slideRunnable)
    }

    private fun restartAutoSlide() {
        stopAutoSlide()
        startAutoSlide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoSlide()
    }

    fun loadData(){
        list.add(IntroData(R.drawable.intro_one))
        list.add(IntroData(R.drawable.intro_two))
        list.add(IntroData(R.drawable.intro_three))
    }

}
