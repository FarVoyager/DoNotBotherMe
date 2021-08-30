package com.example.donotbotherme.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.view.CONDITION_LIST
import java.lang.StringBuilder

class CallListener : BroadcastReceiver() {

    var phoneNumber = "empty"
    lateinit var conditionsList: ArrayList<DisturbCondition>
    private var isProgramSetSilent = false
    var mustRemainSilent = false


    override fun onReceive(context: Context?, intent: Intent?) {
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
            val formattedNumber = formatNumber(conditionsList[i].contactNumber.toString())
            if (formattedNumber == phoneNumber) {
                changePhoneState(context)
            }
        }
    }

    private fun formatNumber(number: String): String {
        val formattedNumber = StringBuilder()
        val numberInChars = number.toCharArray()
        for (i in 0 until numberInChars.size) {
            if (numberInChars[i] == '0' || numberInChars[i] == '1' || numberInChars[i] == '2' || numberInChars[i] == '3' || numberInChars[i] == '4' || numberInChars[i] == '5' || numberInChars[i] == '6' || numberInChars[i] == '7' || numberInChars[i] == '8' || numberInChars[i] == '9') {
                formattedNumber.append(numberInChars[i])
            }
        }
        return formattedNumber.toString()
    }

    private fun changePhoneState(context: Context?) {
        val audioManager: AudioManager =
            context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentPhoneState = audioManager.ringerMode
        //действия при начале звонка
        if (!isProgramSetSilent) {
            if (currentPhoneState != AudioManager.RINGER_MODE_SILENT) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                isProgramSetSilent = true
            } else {
                mustRemainSilent = true
                isProgramSetSilent = true
            }
            //действия при окончании звонка
        } else {
            if (!mustRemainSilent) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                isProgramSetSilent = false
                mustRemainSilent = false
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                isProgramSetSilent = false
                mustRemainSilent = false
            }
        }
    }
}