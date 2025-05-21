package uz.mrx.aripro.presentation.ui.screen.fragment.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenMagazineDetailBinding

@AndroidEntryPoint
class MagazineDetailScreen:Fragment(R.layout.screen_magazine_detail) {


    private val binding:ScreenMagazineDetailBinding by viewBinding(ScreenMagazineDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}