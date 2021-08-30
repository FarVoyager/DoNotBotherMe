package com.example.donotbotherme.app

import android.app.Application
import android.content.Intent

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startService(Intent(this, BackgroundWorkService::class.java))
    }
}