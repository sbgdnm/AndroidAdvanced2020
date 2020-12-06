package com.sbgdnm.yummyfood.utils

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object Constants {

    // Firebase Constants
    // users коллекции в firestore
    const val USERS: String = "users"

    //постоянные переменные для shared preferences и Username Key
    const val MY_YF_PREFERENCES: String = "MyYFPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    // Intent extra constants.
    const val EXTRA_USER_DETAILS: String = "extra_user_details"


    //Уникальный код для запроса разрешения на чтение хранилища с помощью этого мы будем проверять и идентифицировать в методе onRequestPermissionsResult в base activity
    const val READ_STORAGE_PERMISSION_CODE = 2


    //уникальный код для выбора изображения из хранилища. Используя этот код, мы идентифицируем URI изображения, как только он будет выбран.
    //Уникальный код выбора изображения из памяти телефона.
    const val PICK_IMAGE_REQUEST_CODE = 1

    //функция для запроса выбрать изображение с помощью уникального кода.(Функция выбора изображения профиля пользователя из памяти телефона.)
    fun showImageChooser(activity: Activity) {
        // для запуска выбора изображения в памяти телефона.(базовый интент)
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Запускает выбор изображения памяти телефона с помощью постоянного кода.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}
