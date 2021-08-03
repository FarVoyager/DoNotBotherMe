package com.example.donotbotherme.view

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
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
import com.example.donotbotherme.databinding.FragmentMainBinding

class ConditionFragment : Fragment() {

    private var _binding: FragmentConditionBinding? = null
    private val binding get() = _binding!!

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

        //сделать checkPermission возвращающим Boolean чтобы контакты не подгружались 3 раза

        checkPermission(
            android.Manifest.permission.READ_CONTACTS,
            "Доступ к контактам",
            "Для работы приложения необходим доступ к контактам"
        )
        checkPermission(
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            "Доступ к настройкам звука",
            "Для работы приложения необходим доступ к настройкам звука"
        )
        checkPermission(
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            "Доступ к настройкам режимов уведомлений",
            "Для работы приложения необходим доступ к настройкам режимов уведомлений"
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(permission: String, title: String, message: String) {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getContacts() //return true
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
                    requestPermission(permission) //return false
                }
            }
        }
    }

    private fun getContacts() {

        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        println("$i BEB")
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneNumber =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        binding.contactsLayout.addView(
                            AppCompatTextView(it).apply {
                                val textToDisplay = "$name $phoneNumber"
                                text = textToDisplay
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getContacts()
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
        @JvmStatic
        fun newInstance() = ConditionFragment()
    }
}