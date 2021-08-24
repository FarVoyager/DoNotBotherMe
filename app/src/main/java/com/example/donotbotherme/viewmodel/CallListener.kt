package com.example.donotbotherme.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.telephony.TelephonyManager
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.view.CONDITION_LIST

class CallListener : BroadcastReceiver() {

    var phoneNumber = "empty"
    lateinit var conditionsList: ArrayList<DisturbCondition>
    var isProgramSetSilent = false

    override fun onReceive(context: Context?, intent: Intent?) {
        println("Call BEB")

        if (intent?.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            val phone = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            println("$phone BEB")
            phoneNumber = phone.toString()
            compareNumber(context)
        }
    }

    private fun compareNumber(context: Context?) {
        val tinyDB = TinyDB(context)
        conditionsList = tinyDB.getListObject(
            CONDITION_LIST,
            DisturbCondition::class.java
        ) as ArrayList<DisturbCondition>
        for (i in 0 until conditionsList.size) {
            if (conditionsList[i].contactNumber.equals(phoneNumber)) {
                changePhoneState(context)
            }
        }
    }

    private fun changePhoneState(context: Context?) {
        val audioManager: AudioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentPhoneState = audioManager.ringerMode
        if (!isProgramSetSilent) {

            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            isProgramSetSilent = true
        } else {
            audioManager.ringerMode = currentPhoneState
        }


    }
}