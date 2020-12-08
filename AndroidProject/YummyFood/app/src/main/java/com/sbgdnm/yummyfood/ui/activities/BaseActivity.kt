package com.sbgdnm.yummyfood.ui.activities

import android.app.Dialog
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sbgdnm.yummyfood.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    //Глобальная переменная для функции двойного обратного нажатия.
    private var doubleBackToExitPressedOnce = false

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

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        //Говорим что прогресс диалог возмем с dialog_progress
        mProgressDialog.setContentView(R.layout.dialog_progress)
        //При необходимости мы можем поменять текст в коде , но по дефолту стоит "Подождите" в values/string
        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //запуск прогресс диалог
        mProgressDialog.show()
    }

   //Функция для того чтоб закрыть progress dialog, после того как был виден пользователю
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    /**
     * Функция для реализации функции двойного обратного нажатия для выхода из приложения.
     */
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}