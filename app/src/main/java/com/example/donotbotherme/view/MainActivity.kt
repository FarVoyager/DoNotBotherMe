package com.example.donotbotherme.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.donotbotherme.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit()
    }
}