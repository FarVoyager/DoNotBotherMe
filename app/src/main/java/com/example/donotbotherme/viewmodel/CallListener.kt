package com.example.donotbotherme.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.view.CONDITION_LIST
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.StringBuilder

//необходимо корректно сравнить текущую дату (время) с датами из условия
//написать логику изменения состояния

const val PREFS_KEY_CALL = "PREFS_KEY_CALL"
const val PREFS_KEY_ORIG_STATE = "PREFS_KEY_ORIG_STATE"


class CallListener : BroadcastReceiver() {

    var phoneNumber = "empty"
    lateinit var conditionsList: ArrayList<DisturbCondition>
    private var isProgramSetSilent = false
    var mustRemainSilent = false
    private var currentFoundContact : DisturbCondition? = null
    var daysStringBuilder = StringBuilder()
    private val isCallEnded = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        println("onReceive BEB")

//        val audioManager: AudioManager =
//            context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val currentPhoneState = audioManager.ringerMode
//        val sharedPreferences = context.getSharedPreferences("phoneStatePrefs", Context.MODE_PRIVATE)
//        sharedPreferences?.edit()?.putInt("currentPhoneState", currentPhoneState)?.apply()
//        sharedPreferences?.edit()?.putBoolean("isCallStarted", isCallStarted)?.apply()

        if (intent?.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            val phone = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            phoneNumber = phone.toString()
            compareConditions(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compareConditions(context: Context?) {

        val tinyDB = TinyDB(context)
        conditionsList = tinyDB.getListObject(
            CONDITION_LIST,
            DisturbCondition::class.java
        ) as ArrayList<DisturbCondition>

        compareNumber(context)
//        compareTime()
//        compareDay(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compareNumber(context: Context?){
        for (i in 0 until conditionsList.size) {
            val formattedNumber = formatNumber(conditionsList[i].contactNumber.toString())
            if (formattedNumber == phoneNumber) {
//                changePhoneState(context)

                currentFoundContact = conditionsList[i]
                compareDay(context)
            } else {
                currentFoundContact = null
                daysStringBuilder = StringBuilder()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compareDay(context: Context?) {

            println("compareDay BEB")
            val currentDay = LocalDate.now().dayOfWeek.name
            getDaysBooleans()
            val daysString = daysStringBuilder.toString()
            val daysList = daysString.split(" ")
            for (i in daysList.indices) {
                if (daysList[i] == currentDay) {
                    changePhoneState(context)
                    break
                } else {
                    currentFoundContact = null
                    daysStringBuilder = StringBuilder()
                }
            }
        }


    private fun getDaysBooleans() {
        appendDaysStringBuilder(currentFoundContact?.isMondayBlocked, "MONDAY")
        appendDaysStringBuilder(currentFoundContact?.isTuesdayBlocked, "TUESDAY")
        appendDaysStringBuilder(currentFoundContact?.isWednesdayBlocked, "WEDNESDAY")
        appendDaysStringBuilder(currentFoundContact?.isThursdayBlocked, "THURSDAY")
        appendDaysStringBuilder(currentFoundContact?.isFridayBlocked, "FRIDAY")
        appendDaysStringBuilder(currentFoundContact?.isSaturdayBlocked, "SATURDAY")
        appendDaysStringBuilder(currentFoundContact?.isSundayBlocked, "SUNDAY")
    }

    private fun appendDaysStringBuilder(dayBoolean: Boolean?, text: String) {
        if (dayBoolean == true) {
            daysStringBuilder.append("$text ")
        }
    }

    private fun getCurrentTimeByStringList() : List<String> {
        val currentDate = Calendar.getInstance().time.toString()
        val timeData = currentDate.split(" ")
        val timeDataSplit = timeData[3].split(":")
        return timeDataSplit
    }

//    private fun compareTime() {
//        if (isNumberFound) {
//            isNumberFound = false
//            val timeStart = currentFoundContact?.timeStart
//            val timeEnd = currentFoundContact?.timeEnd
//
//            val timeDataSplit = getCurrentTimeByStringList()
//            val currentTime = timeDataSplit[0] + ":" + timeDataSplit[1]
//            println("$currentTime BEBOO")
//
//            compareDates(timeStart, timeEnd)
//
//        }
//    }


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

        println("changePhoneState BEB")
        val audioManager: AudioManager =
            context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentPhoneState = audioManager.ringerMode

        val sharedPreferences = context.getSharedPreferences("callPrefs", Context.MODE_PRIVATE)
        val callState = sharedPreferences?.getBoolean(PREFS_KEY_CALL, false)

        //начало звонка
        if (callState == null || callState == false) {
            if (currentPhoneState == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            } else if (currentPhoneState == AudioManager.RINGER_MODE_SILENT) {
                //do nothing
            }
            sharedPreferences.edit().putBoolean(PREFS_KEY_CALL, isCallEnded).apply()
            sharedPreferences.edit().putInt(PREFS_KEY_ORIG_STATE, currentPhoneState).apply()
        }
        //конец звонка
        else if (callState == true) {
            val originalState = sharedPreferences.getInt(PREFS_KEY_ORIG_STATE, -1)
            if (currentPhoneState == AudioManager.RINGER_MODE_NORMAL) {
                //do nothing
            } else if (currentPhoneState == AudioManager.RINGER_MODE_SILENT) {
                audioManager.ringerMode = originalState
            }
            sharedPreferences.edit().putBoolean(PREFS_KEY_CALL, !isCallEnded).apply()
        }




//        //действия при начале звонка
//        if (!isProgramSetSilent) {
//            if (currentPhoneState != AudioManager.RINGER_MODE_SILENT) {
//                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
//                isProgramSetSilent = true
//            } else {
//                mustRemainSilent = true
//                isProgramSetSilent = true
//            }
//            //действия при окончании звонка
//        } else {
//            if (!mustRemainSilent) {
//                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//                isProgramSetSilent = false
//                mustRemainSilent = false
//            } else {
//                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
//                isProgramSetSilent = false
//                mustRemainSilent = false
//            }
//        }
    }
}