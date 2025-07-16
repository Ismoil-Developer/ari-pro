package uz.mrx.aripro.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
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

    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Bu yerda internet o'zgarishini kuzatishingiz mumkin
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerConnectivityReceiver()
        startForegroundServiceWithNotification()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private fun registerConnectivityReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(connectivityReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(connectivityReceiver, filter)
            }
        } catch (e: SecurityException) {
            Log.e("CourierService", "Receiver ro'yxatdan o'tkazishda xatolik: ${e.message}")
        }
    }

    private fun createNotificationChannel(channelId: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                name,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "location_service_channel"
        val channelName = "Courier Location Service"

        createNotificationChannel(channelId, channelName)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Joylashuv kuzatilmoqda")
            .setContentText("Joylashuvingiz real vaqtda kuzatilmoqda.")
            .setSmallIcon(R.drawable.ic_location_det)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("CourierService", "Joylashuv ruxsatlari mavjud emas")
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location = result.lastLocation ?: return
                sendLocationOverWebSocket(location)
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
            Log.e("CourierService", "JSON yuborishda xatolik: ${e.message}")
        }
    }

    override fun onDestroy() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        try {
            unregisterReceiver(connectivityReceiver)
        } catch (e: Exception) {
            Log.e("CourierService", "Receiver o'chirishda xatolik: ${e.message}")
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
