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
     * A function to load image from Uri or URL for the product image.
     */
    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // Uri or URL of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
