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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//необходимо корректно сравнить текущую дату (время) с датами из условия
class CallListener : BroadcastReceiver() {

    var phoneNumber = "empty"
    lateinit var conditionsList: ArrayList<DisturbCondition>
    private var isProgramSetSilent = false
    var mustRemainSilent = false
    var isNumberFound = false
    var currentFoundContact : DisturbCondition? = null


    override fun onReceive(context: Context?, intent: Intent?) {
        println("onReceive BEB")
        if (intent?.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            val phone = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            println("$phone BEB")
            phoneNumber = phone.toString()
            compareConditions(context)
        }
    }

    private fun compareConditions(context: Context?) {

        val tinyDB = TinyDB(context)
        conditionsList = tinyDB.getListObject(
            CONDITION_LIST,
            DisturbCondition::class.java
        ) as ArrayList<DisturbCondition>

        compareNumber(context)
        compareTime(context)
//        compareDay()
    }

    private fun compareTime(context: Context?) {
        if (isNumberFound) {
            isNumberFound = false
            val timeStart = currentFoundContact?.timeStart
            val timeEnd = currentFoundContact?.timeEnd

            val currentDate = Calendar.getInstance().time.toString()
            val timeData = currentDate.split(" ")
            val timeDataSplit = timeData[3].split(":")
            val currentTime = timeDataSplit[0] + ":" + timeDataSplit[1]
            println("$currentTime BEBOO")

//            compareDates(timeStart, timeEnd)

        }
    }


    private fun compareDates(str_date1:String?, str_date2:String?):Int {
        lateinit var date1:Date
        lateinit var date2:Date

        if (str_date1 != null && str_date2 != null) {
            if (str_date1.isNotEmpty() && str_date2.isNotEmpty()) {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                date1 = formatter.parse(str_date1)
                date2 = formatter.parse(str_date2)
                println("$date1 BEB")
                println("$date2 BEB")

            }
        }
        return date1.compareTo(date2)
    }


    private fun compareNumber(context: Context?){
        for (i in 0 until conditionsList.size) {
            val formattedNumber = formatNumber(conditionsList[i].contactNumber.toString())
            if (formattedNumber == phoneNumber) {
                changePhoneState(context)
                isNumberFound = true
                currentFoundContact = conditionsList[i]
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