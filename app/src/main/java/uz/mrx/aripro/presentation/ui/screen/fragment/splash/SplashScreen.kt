package uz.mrx.aripro.presentation.ui.screen.fragment.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.presentation.ui.viewmodel.splash.SplashScreenViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.splash.impl.SplashScreenViewModelImpl
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : Fragment(R.layout.screen_splash) {

    private val viewModel: SplashScreenViewModel by viewModels<SplashScreenViewModelImpl>()

    @Inject
    lateinit var sharedPref: MySharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        Log.d("TTTTTTTTTT", "onViewCreated: ${sharedPref.token}")

        if (sharedPref.token.isNotEmpty()){
            viewModel.openMainScreen()
        }else{
            viewModel.openScreen()
        }

    }

}