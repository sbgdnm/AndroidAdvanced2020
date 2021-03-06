package com.sbgdnm.yummyfood.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sbgdnm.yummyfood.R
import java.io.IOException

// Создем пользовательский объект для создания общих функций для Glide, которые можно использовать во всем приложении.
class GlideLoader(val context: Context) {

    //функция для загрузки изображения из URI для изображения профиля пользователя.
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            // Загрузите изображение пользователя в ImageView.
            Glide
                .with(context)
                .load(image) // URI изображения
                .centerCrop() // Масштабный тип изображения.
                .placeholder(R.drawable.ic_user_placeholder) //  по умолчанию осстается изображение, если изображение не удалось загрузить.
                .into(imageView) // вид, в котором будет загружено изображение.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    /**
     * Функция загрузки изображения из Uri или URL-адреса для изображения продукта.
     */
    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            //Загрузите изображение пользователя в ImageView.
            Glide
                .with(context)
                .load(image) //URI изображения
                .centerCrop() //Масштабный тип изображения.
                .into(imageView) // вид, в котором будет загружено изображение.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
