package ru.hivislav.simpleweather.view.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_contacts.*
import ru.hivislav.simpleweather.R

class ContactsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    //функция для проверки наличия нужных разрешений
    private fun checkPermission() {
        context?.let {
            when {
                //если доступ к контактам есть
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                            getContacts()
                        }

                //если доступа нет или он был отменен -> запрашиваем рационализацию
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    showDialog()
                }

                //запрашиваем разрешение
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    //результат запроса на разрешение доступа
    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {isGranted ->
        if (isGranted) {
            getContacts()
        } else {
            Toast.makeText(context, "Нужно разрешение на чтение контактов", Toast.LENGTH_SHORT).show()
        }
    }


    //функция запроса для получения разрешения
    private fun myRequestPermission() {
        permissionResult.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage("Очень нужно, иначе дело плохо")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                myRequestPermission()
            }
            .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getContacts() {
        context?.let {
            /**
            Отправляем запрос на получение контактов к контент-провайдеру
            через contentResolver и получаем ответ в виде Cursor'a
            **/
            val cursorWithContacts: Cursor? = it.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )

            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    //переходим на позицию в Cursor'e
                    if (cursor.moveToPosition(i)) {
                        //Берём из Cursor'a столбец с именем
                        val columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        if (columnIndex >= 0) {
                            val name = cursor.getString(columnIndex)
                            addView(name ?: "пустое имя")
                        }
                    }
                }
            }
            cursorWithContacts?.close()
        }
    }

    private fun addView(name: String) {
        containerForContacts.addView(TextView(requireContext()).apply {
            text = name
        })
    }

    companion object {
        fun newInstance() = ContactsFragment()
    }
}