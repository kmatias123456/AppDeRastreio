package com.example.androidessncial.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.androidessncial.R
import com.example.androidessncial.service.ServiceLocationBackApp

class SmsServiceLocationBack : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {
        when (intent?.action) {
            Actions.START.toString() -> "00:50".start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun String.start() {
        val toastText = "The count is $this"

        Toast.makeText(
            this@SmsServiceLocationBack,
            toastText,
            Toast.LENGTH_SHORT
        ).show()

        val processingIntent = Intent(
            this@SmsServiceLocationBack,
            ServiceLocationBackApp::class.java
        )

        startService(processingIntent)

        val notification = NotificationCompat.Builder(
            this@SmsServiceLocationBack,
            "running_channel"
        )
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle("Run is active")
            .setContentText("The count is $this")
            .build()
        startForeground(1, notification)
    }

    enum class Actions {
        START, STOP
    }
}
