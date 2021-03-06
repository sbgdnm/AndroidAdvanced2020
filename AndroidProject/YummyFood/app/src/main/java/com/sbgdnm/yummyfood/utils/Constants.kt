package com.sbgdnm.yummyfood.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    // Firebase Constants
    // users и products коллекции в firestore
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val DASHBOARD_PRODUCTS: String = "dashboard"
    const val ORDERS: String = "orders"

    //постоянные переменные для shared preferences и Username Key
    const val MY_YF_PREFERENCES: String = "MyYFPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"



    //Уникальный код для запроса разрешения на чтение хранилища с помощью этого мы будем проверять и идентифицировать в методе onRequestPermissionsResult в base activity
    const val READ_STORAGE_PERMISSION_CODE = 2


    //уникальный код для выбора изображения из хранилища. Используя этот код, мы идентифицируем URI изображения, как только он будет выбран.
    //Уникальный код выбора изображения из памяти телефона.
    const val PICK_IMAGE_REQUEST_CODE = 1
    //постоянные переменные для фио
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    // Постоянные переменные для "Пола"
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"

    // Firebase database имена полей базы данных
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    //профиль фото
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val IMAGE: String = "image"

    //константа ждя того чтобы проверить первый ли раз зашел на аккаунт пользоваетель
    const val COMPLETE_PROFILE: String = "profileCompleted"

    //постоянные для фото рецепта
    const val PRODUCT_IMAGE: String = "MyRecipe_Product_Image"

    //для робыты с рецептами который ввел один пользователь , так же он понадобится для показа заказов еды одного пользователя
    const val USER_ID: String = "user_id"


    //постоянная переменная для передачи идентификатора продукта на экран сведений о продукте через intent.
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"

    // постоянная переменная для количества корзины по умолчанию.
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_QUANTITY: String = "cart_quantity"
    //постоянная переменная для сбора элементов корзины.
    const val CART_ITEMS: String = "cart_items"
    //постаянная переменная  для идентификатора продукта.
    const val PRODUCT_ID: String = "product_id"

    // constants for the address types.
    const val HOME: String = "Дом"
    const val OFFICE: String = "Офис"
    const val OTHER: String = "Другое"
    // constants for the addresses collection.
    const val ADDRESSES: String = "addresses"
    //  constant value to pass the address details through intent.
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_MY_ORDER_DETAILS"

    const val STOCK_QUANTITY: String = "stock_quantity"




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

    //Функция для получения расширения файла изображения выбранного изображения.
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Двусторонняя карта, которая сопоставляет MIME-типы с расширениями файлов и наоборот.
         *
         * getSingleton():Получите одноэлементный экземпляр MimeTypeMap.
         *
         * getExtensionFromMimeType: Возвращает зарегистрированное расширение для данного типа MIME.
         *
         * contentResolver.getType: Возвращает тип MIME данного URL-адреса .
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
