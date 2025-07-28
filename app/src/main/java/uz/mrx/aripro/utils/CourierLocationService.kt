package uz.mrx.aripro.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForegroundServiceWithNotification()
        startLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundServiceWithNotification() {
        val channelId = "location_channel"
        createNotificationChannel(channelId, "Courier Location Updates")

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Joylashuv yuborilmoqda")
            .setContentText("Sizning joylashuvingiz yangilanmoqda...")
            .setSmallIcon(R.drawable.ic_location_det)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("CourierService", "📍 Joylashuv permission yo'q!")
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                sendLocationOverWebSocket(location)

                // Lokatsiyani Toast orqali chiqarish
                Toast.makeText(
                    this@CourierLocationService,
                    "📍 Lat: ${location.latitude}, Lon: ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun sendLocationOverWebSocket(location: Location) {
        try {
            val json = JSONObject().apply {
                put("action", "location_update")
                put("latitude", location.latitude)
                put("longitude", location.longitude)
            }
            webSocketClient.sendMessage(json.toString())
        } catch (e: Exception) {
            Log.e("CourierService", "❌ JSON yuborishda xatolik: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}
