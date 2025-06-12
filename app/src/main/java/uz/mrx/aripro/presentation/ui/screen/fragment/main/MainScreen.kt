package uz.mrx.aripro.presentation.ui.screen.fragment.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.ScreenMainBinding
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.HomePageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.impl.HomePageViewModelImpl

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {

    private val binding: ScreenMainBinding by viewBinding(ScreenMainBinding::bind)
    private val homePageViewModel:HomePageViewModel by viewModels<HomePageViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.confirmPage -> {
                    homePageViewModel.openOrderDeliveryScreen(-1)
                    true
                }
                else -> {
                    // Default behavior for other tabs
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

    }
}