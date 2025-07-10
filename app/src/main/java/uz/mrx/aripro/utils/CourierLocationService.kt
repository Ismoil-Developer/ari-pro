package uz.mrx.aripro.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import uz.mrx.aripro.R
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient
import javax.inject.Inject

@AndroidEntryPoint
class CourierLocationService : Service() {

    @Inject
    lateinit var webSocketClient: CourierWebSocketClient

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private fun startForegroundService() {
        val channelId = "location_channel"

        // Create notification channel only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Updates",
                NotificationManager.IMPORTANCE_DEFAULT // yoki HIGH
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("AriPro")
            .setContentText("Kuryer lokatsiyasi yuborilmoqda...")
            .setSmallIcon(R.drawable.ic_location_det)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Qo‘shing
            .build()


        startForeground(1, notification)
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation ?: return

                val json = JSONObject().apply {
                    put("action", "location_update")
                    put("latitude", location.latitude)
                    put("longitude", location.longitude)
                }

                webSocketClient.sendMessage(json.toString())
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                // Optional: show user a message or log the issue
            }
        } else {
            // Optional: Request permission or notify user
            Log.w("Location", "Location permission not granted")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
