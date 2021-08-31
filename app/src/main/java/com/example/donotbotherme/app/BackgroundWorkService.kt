package com.example.donotbotherme.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.donotbotherme.R
import com.example.donotbotherme.view.MainActivity
//режим не переключается при уничтоженном процессе

class BackgroundWorkService : Service() {

    private val NOTIF_ID = 1
    private val NOTIF_CHANNEL_ID = "CHANNEL_ID"


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initChannel(this)
        startServiceForeground()
        return START_STICKY
    }

    private fun initChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) { return }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIF_CHANNEL_ID,
            "Channel for service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Channel description"
        notificationManager.createNotificationChannel(channel)
    }

    private fun startServiceForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        startForeground(
            NOTIF_ID, NotificationCompat.Builder(
                this,
                NOTIF_CHANNEL_ID
            )
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }
}