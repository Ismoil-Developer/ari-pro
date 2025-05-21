package uz.mrx.aripro.presentation.ui.screen.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.mrx.aripro.R
import uz.mrx.aripro.presentation.navigation.NavigationHandler
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var handler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        handler.navigationStack.onEach {
            it.invoke(navHost.navController)
        }.launchIn(lifecycleScope)

    }
}