package com.example.donotbotherme.view

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.example.donotbotherme.R
import com.example.donotbotherme.viewmodel.CallListener

private val receiver = CallListener()

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Activity BEB")

//        registerReceiver(receiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))

        val notificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit()
    }

    override fun onDestroy() {
//        unregisterReceiver(receiver)
        super.onDestroy()
    }
}