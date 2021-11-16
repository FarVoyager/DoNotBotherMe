package com.example.donotbotherme.app

import android.app.Application
import android.content.Intent
import com.example.donotbotherme.app.di.KoinDI
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(KoinDI.getDatabaseModule(), KoinDI.getCoreModule())
        }
    }
}