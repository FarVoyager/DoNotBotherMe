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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.donotbotherme.R
import com.example.donotbotherme.databinding.FragmentConditionBinding
import com.example.donotbotherme.model.DisturbCondition
const val CONDITIONS_ARRAYLIST = "CONDITIONS_ARRAYLIST"

class ConditionFragment : Fragment() {

    private var _binding: FragmentConditionBinding? = null
    private val binding get() = _binding!!

    private var chosenContactNumber: String? = null
    private var chosenContactName: String? = null



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

        val conditionsList = ArrayList<DisturbCondition>()

        //запрос разрешений
        checkPermission(android.Manifest.permission.READ_CONTACTS, TITLE_CONTACTS, MSG_CONTACTS)
        checkPermission(android.Manifest.permission.MODIFY_AUDIO_SETTINGS, TITLE_AUDIO, MSG_AUDIO)
        checkPermission(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY, TITLE_NOTIFICATION, MSG_NOTIFICATION)

        //действие при нажатии на кнопку "Готово"
        binding.buttonDone.setOnClickListener {
            val contactName = chosenContactName
            val contactNumber = chosenContactNumber
            val startTime = binding.startTimeEditText.text.toString()
            val endTime = binding.endTimeEditText.text.toString()
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

            conditionsList.add(condition)

            val intent = Intent(requireActivity(), MainFragment::class.java)
            intent.putExtra("conditions", conditionsList)

            val retrievedData = intent.getParcelableArrayListExtra<Parcelable>("conditions")

            println("$retrievedData BEBA")

            parentFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit()
        }
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
                        println("$i BEB")
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneNumber =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        //по каждому контакту создаем textView
                        binding.contactsLayout.addView(
                            AppCompatTextView(it).apply {
                                id = i
                                val textToDisplay = "$name $phoneNumber"
                                setBackgroundColor(resources.getColor(R.color.white))
                                text = textToDisplay

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
        const val MSG_NOTIFICATION = "Для работы приложения необходим доступ к настройкам режимов уведомлений"

        @JvmStatic
        fun newInstance() = ConditionFragment()
    }
}