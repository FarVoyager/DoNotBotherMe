package com.example.donotbotherme.app

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BackgroundWorkService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do smth
        return super.onStartCommand(intent, flags, startId)
    }
}