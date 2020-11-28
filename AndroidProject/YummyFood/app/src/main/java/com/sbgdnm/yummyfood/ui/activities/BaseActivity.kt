package com.sbgdnm.yummyfood.ui.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sbgdnm.yummyfood.R

open class BaseActivity : AppCompatActivity() {
    //Когда при заполнении полей , например , при регистрации выходит ошибка целостности аккаунта при регистрации
    //и эти ошибки проходят через эту функцию принимая цвет свой в зависимоти от это error или все окей)))
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }
}