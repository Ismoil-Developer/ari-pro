package uz.mrx.aripro.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("79fb340d-f68a-4f8b-b729-8f19f413786d") // API key
        MapKitFactory.initialize(this)
        TransportFactory.initialize(this)


        instance = this
    }

}