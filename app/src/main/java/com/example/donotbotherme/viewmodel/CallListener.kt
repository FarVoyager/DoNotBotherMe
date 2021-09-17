package com.example.donotbotherme.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.view.CONDITION_LIST
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.StringBuilder

const val PREFS_KEY_CALL = "PREFS_KEY_CALL"
const val PREFS_KEY_ORIG_STATE = "PREFS_KEY_ORIG_STATE"

class CallListener : BroadcastReceiver() {

    var phoneNumber = "empty"
    lateinit var conditionsList: ArrayList<DisturbCondition>
    private var currentFoundContact : DisturbCondition? = null
    var daysStringBuilder = StringBuilder()
    private val isCallEnded = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        println("onReceive BEB")
        if (intent?.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            val phone = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            phoneNumber =  formatNumber(phone.toString())
            println("phoneNumber: $phoneNumber BEB")

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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compareNumber(context: Context?){
        println("compareNumber BEB")
        for (i in 0 until conditionsList.size) {
            val formattedNumber = formatNumber(conditionsList[i].contactNumber.toString())
            val formattedNumberFirstSeven = formattedNumber.replaceFirstChar {
                '7'
            }
            val formattedNumberFirstEight = formattedNumber.replaceFirstChar {
                '8'
            }
            if (formattedNumberFirstEight == phoneNumber || formattedNumberFirstSeven == phoneNumber) {

                currentFoundContact = conditionsList[i]
                Toast.makeText(context, "Number Found BEB", Toast.LENGTH_SHORT).show()
                compareTime(context)

            } else {
                Toast.makeText(context, "Number NOT Found BEB", Toast.LENGTH_SHORT).show()

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

        @RequiresApi(Build.VERSION_CODES.O)
        private fun compareTime(context: Context?) {
            println("compareTime BEB")

            //вычленение значений часов и минут
            val timeStart = currentFoundContact?.timeStart
            val timeStartValues = timeStart?.split(".")
            val timeStartHours = timeStartValues?.get(0)?.toInt()
            val timeStartMinutes = timeStartValues?.get(1)?.toInt()
            val timeEnd = currentFoundContact?.timeEnd
            val timeEndValues = timeEnd?.split(".")
            val timeEndHours = timeEndValues?.get(0)?.toInt()
            val timeEndMinutes = timeEndValues?.get(1)?.toInt()
            val currentTimeDataSplit = getCurrentTimeByStringList()
            val currentHours = currentTimeDataSplit[0].toInt()
            val currentMinutes = currentTimeDataSplit[1].toInt()

            //сравнение текущего времени и времени условий
            if (timeStartHours == null || timeEndHours == null || timeStartMinutes == null || timeEndMinutes == null) {
                Toast.makeText(context, "something in end/start is null", Toast.LENGTH_SHORT).show()
            } else if (currentHours > timeStartHours && currentHours < timeEndHours) {
                compareDay(context)
            } else if (currentHours == timeStartHours) {
                if (currentMinutes > timeStartMinutes) {
                    compareDay(context)
                }
            } else if (currentHours == timeEndHours) {
                if (currentMinutes < timeEndMinutes) {
                    compareDay(context)
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

    private fun formatNumber(number: String): String {
        val formattedNumber = StringBuilder()
        val numberInChars = number.toCharArray()
        for (i in numberInChars.indices) {
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
    }
}