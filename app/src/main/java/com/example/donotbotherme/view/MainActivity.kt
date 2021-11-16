package com.example.donotbotherme.view


//ЗАДАЧИ
//- отключить поворот экрана // сделал locked в манифесте, DONE
//- предотвращать некорректный ввод времени (начало позже конца) // DONE
//-предотвращать наложение нескольких событий друг на друга
//- сделать окно сведений об условии
//- избавиться от копий контактов в списке
//- проверить наличие ненужных разрешений
//- сделать нормальный запрос разрешений, а не как сейчас
//- сделать список условий информативнее и читабельнее
//- реализовать функцию удаления конкретного события // DONE
//- сделать фрагмент создания условия читабельнее
//- поработать над UI в целом
//- забацать иконку и Splash screen

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.donotbotherme.R

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Activity BEB")

        val notificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit()
    }
}