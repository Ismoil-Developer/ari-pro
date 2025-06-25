package uz.mrx.aripro.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import uz.mrx.aripro.data.local.shp.MySharedPreference
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    @Inject
    lateinit var sharedPreference: MySharedPreference

    override fun onCreate() {
        super.onCreate()

        instance = this

        MapKitFactory.setApiKey("79fb340d-f68a-4f8b-b729-8f19f413786d")
        MapKitFactory.initialize(this)

        // Lingver init
        val languageCode = sharedPreference.language // "uz", "en", "ru", etc.
        Lingver.init(this, languageCode)

    }

}