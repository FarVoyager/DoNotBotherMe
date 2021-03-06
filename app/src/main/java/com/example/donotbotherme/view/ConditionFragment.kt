package com.example.donotbotherme.view

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.donotbotherme.R
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.databinding.FragmentConditionBinding
import com.example.donotbotherme.interactors.MainInteractor
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.model.room.RoomRepository
import com.example.donotbotherme.view.main.CONDITION_LIST
import com.example.donotbotherme.view.main.IS_CONDITION_CREATED
import com.example.donotbotherme.view.main.MainFragment
import com.example.donotbotherme.view.main.REQUEST_CODE

const val NEW_CONDITION = "NEW_CONDITION"

class ConditionFragment : Fragment() {

    private var _binding: FragmentConditionBinding? = null
    private val binding get() = _binding!!

    private var chosenContactNumber: String? = null
    private var chosenContactName: String? = null
    private var isContactChosen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //запрос разрешений
        checkPermission(android.Manifest.permission.READ_CONTACTS, TITLE_CONTACTS, MSG_CONTACTS)
        checkPermission(android.Manifest.permission.MODIFY_AUDIO_SETTINGS, TITLE_AUDIO, MSG_AUDIO)
        checkPermission(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY, TITLE_NOTIFICATION, MSG_NOTIFICATION)
        checkPermission(
            android.Manifest.permission.READ_PHONE_STATE,
            TITLE_PHONE_STATE,
            MSG_PHONE_STATE
        )
        checkPermission(android.Manifest.permission.READ_CALL_LOG, TITLE_READ_LOG, MSG_READ_LOG)


        //действие при нажатии на кнопку "Готово"
        binding.buttonDone.setOnClickListener {

            if (!isContactChosen) {
                Toast.makeText(requireContext(), "Контакт не выбран", Toast.LENGTH_SHORT).show()
            }
            //если поля не пусты
            else if (!binding.startTimeHourEditText.text.isNullOrEmpty() &&
                !binding.endTimeHourEditText.text.isNullOrEmpty() &&
                !binding.startTimeMinuteEditText.text.isNullOrEmpty() &&
                !binding.endTimeMinuteEditText.text.isNullOrEmpty()
            ) {

//                if (compareConditionsOfEqualNumbers()) {

                //если числа больше допустимых или начало позже конца
                if (parseIntValue(binding.startTimeHourEditText.text.toString()) > 23 ||
                    parseIntValue(binding.endTimeHourEditText.text.toString()) > 23 ||
                    parseIntValue(binding.startTimeMinuteEditText.text.toString()) > 59 ||
                    parseIntValue(binding.endTimeMinuteEditText.text.toString()) > 59 ||
                    parseIntValue(binding.startTimeHourEditText.text.toString()) > parseIntValue(
                        binding.endTimeHourEditText.text.toString()
                    )
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Неверный формат времени",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (
                    !binding.checkSunday.isChecked &&
                    !binding.checkMonday.isChecked &&
                    !binding.checkTuesday.isChecked &&
                    !binding.checkWednesday.isChecked &&
                    !binding.checkThursday.isChecked &&
                    !binding.checkFriday.isChecked &&
                    !binding.checkSaturday.isChecked
                ) {
                    Toast.makeText(requireContext(), "Не выбраны дни", Toast.LENGTH_SHORT).show()
                } else {
                    val contactName = chosenContactName
                    val contactNumber = chosenContactNumber
                    val startTime =
                        binding.startTimeHourEditText.text.toString() + "." + binding.startTimeMinuteEditText.text.toString()
                    val endTime =
                        binding.endTimeHourEditText.text.toString() + "." + binding.endTimeMinuteEditText.text.toString()
                    val isMonday = binding.checkMonday.isChecked
                    val isTuesday = binding.checkTuesday.isChecked
                    val isWednesday = binding.checkWednesday.isChecked
                    val isThursday = binding.checkThursday.isChecked
                    val isFriday = binding.checkFriday.isChecked
                    val isSaturday = binding.checkSaturday.isChecked
                    val isSunday = binding.checkSunday.isChecked

                    val condition = DisturbCondition(
                        contactName,
                        contactNumber,
                        startTime,
                        endTime,
                        isMonday,
                        isTuesday,
                        isWednesday,
                        isThursday,
                        isFriday,
                        isSaturday,
                        isSunday
                    )

                    val bundle = Bundle()
                    bundle.putParcelable(NEW_CONDITION, condition)
                    bundle.putInt(IS_CONDITION_CREATED, 1)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, MainFragment.newInstance(bundle))
                        .commit()
                }
//                } else {
//                    Toast.makeText(requireContext(), "BITCH", Toast.LENGTH_SHORT)
//                        .show()
//                }
            } else {
                Toast.makeText(requireContext(), "Некоторые поля пусты", Toast.LENGTH_SHORT)
                    .show()

            }
        }

    }

    //где-то ошибочная логика
    private fun compareConditionsOfEqualNumbers(): Boolean {
        var isTimeOk = false
        var isDaysOk = false

        val tinyDB = TinyDB(requireContext())
        val conditionsList = tinyDB.getListObject(
            CONDITION_LIST,
            DisturbCondition::class.java
        ) as ArrayList<DisturbCondition>
        println(conditionsList.size.toString() + " BEBSIZE")
        for (i in 0 until conditionsList.size) {

            println("$i BEBS")
            val timeStart = conditionsList[i].timeStart
            val timeStartValues = timeStart?.split(".")
            val timeStartHours = timeStartValues?.get(0)?.toInt()
            val timeStartMinutes = timeStartValues?.get(1)?.toInt()
            val timeEnd = conditionsList[i].timeEnd
            val timeEndValues = timeEnd?.split(".")
            val timeEndHours = timeEndValues?.get(0)?.toInt()
            val timeEndMinutes = timeEndValues?.get(1)?.toInt()

            if (conditionsList[i].contactNumber == chosenContactNumber) {
                if (parseIntValue(binding.startTimeHourEditText.text.toString()) > timeStartHours!! &&
                    parseIntValue(binding.startTimeHourEditText.text.toString()) < timeEndHours!!
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Указанное время конфликтует с уже созданным условием",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (parseIntValue(binding.endTimeHourEditText.text.toString()) > timeStartHours &&
                    parseIntValue(binding.endTimeHourEditText.text.toString()) < timeEndHours!!
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Указанное время конфликтует с уже созданным условием",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    isTimeOk = true
                }
            } else {
                isTimeOk = true
            }
            if (conditionsList[i].isMondayBlocked && binding.checkMonday.isChecked ||
                conditionsList[i].isTuesdayBlocked && binding.checkTuesday.isChecked ||
                conditionsList[i].isWednesdayBlocked && binding.checkWednesday.isChecked ||
                conditionsList[i].isThursdayBlocked && binding.checkThursday.isChecked ||
                conditionsList[i].isFridayBlocked && binding.checkFriday.isChecked ||
                conditionsList[i].isSaturdayBlocked && binding.checkSaturday.isChecked ||
                conditionsList[i].isSundayBlocked && binding.checkSunday.isChecked
            ) {
                Toast.makeText(
                    requireContext(),
                    "Указанные дни конфликтуют с уже созданным условием",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                isDaysOk = true
            }
        }
        if (conditionsList.size == 0) {
            println("return TRUE BEB empty list")

            return true
        }

        if (isDaysOk && isTimeOk) {
            println("return TRUE BEB")
            return true
        }
        println("return FALSE BEB ($isTimeOk $isDaysOk")
        return false
    }

    private fun parseIntValue(string: String): Int {
        return string.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(permission: String, title: String, message: String) {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, permission) ==
                        PackageManager.PERMISSION_GRANTED && permission == android.Manifest.permission.READ_CONTACTS -> {
                    getContacts()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    AlertDialog.Builder(it)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            requestPermission(permission) //return false
                        }
                        .setNegativeButton("Не надо") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create().show()
                }
                else -> {
                    requestPermission(permission)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getContacts() {

        //курсором получаем строки контактов
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            //вычленяем имя и номер контакта
            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneNumber =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        //по каждому контакту создаем textView
                        binding.contactsLayout.addView(
                            AppCompatTextView(it).apply {
                                id = i
                                val textToDisplay = "$name  $phoneNumber"
                                setBackgroundColor(resources.getColor(R.color.white))
                                text = textToDisplay
                                gravity = Gravity.CENTER_VERTICAL
                                height = 80
                                textSize = 14f

                                setOnClickListener {
                                    binding.contactsLayout.let {
                                        for (l in 0 until it.childCount) {
                                            val childView: View = it.getChildAt(l)
                                            childView.setBackgroundColor(Color.WHITE)
                                        }
                                    }
                                    setBackgroundColor(Color.LTGRAY)
                                    chosenContactNumber = phoneNumber
                                    chosenContactName = name
                                    println(chosenContactNumber.toString() + "BEB")
                                    isContactChosen = true
                                }
                            }
                        )
                    }
                }
            }
            cursorWithContacts?.close()
        }
    }

    private fun requestPermission(permission: String) {
        requestPermissions(arrayOf(permission), REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    permissions[0] == android.Manifest.permission.READ_CONTACTS
                ) {
                    getContacts()
                } else if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    permissions[0] != android.Manifest.permission.READ_CONTACTS
                ) {
                    //do nothing, looks like other two permissions are granted by default
                } else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Требуется доступ")
                            .setMessage("Для использования функций приложения требуются запрошенные разрешения")
                            .setNegativeButton("Закрыть") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create().show()
                    }
                }
            }
        }
    }

    companion object {
        const val TITLE_CONTACTS = "Доступ к контактам"
        const val MSG_CONTACTS = "Для работы приложения необходим доступ к контактам"
        const val TITLE_AUDIO = "Доступ к настройкам звука"
        const val MSG_AUDIO = "Для работы приложения необходим доступ к настройкам звука"
        const val TITLE_NOTIFICATION = "Доступ к настройкам режимов уведомлений"
        const val MSG_NOTIFICATION =
            "Для работы приложения необходим доступ к настройкам режимов уведомлений"
        const val TITLE_PHONE_STATE = "Доступ к обработке входящего вызова"
        const val MSG_PHONE_STATE =
            "Для работы приложения необходим доступ к обработке входящих вызовов"
        const val TITLE_READ_LOG = "Доступ к настройкам чтения данных входящего вызова"
        const val MSG_READ_LOG =
            "Для работы приложения необходим доступ к настройкам чтения данных входящих вызовов"

        @JvmStatic
        fun newInstance() = ConditionFragment()
    }
}