package com.sbgdnm.yummyfood.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.R
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Для того чтобы активити занимал весь экран но у меня что то не работе
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

    }
    //В Login Activity кликейбл кнопки являются login button , забыли пароль и регистарция текст
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.tv_forgot_password -> {
                    //при нажатии переход на забыли пароль
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser() //Авторизация
                }

                R.id.tv_register -> {
                    //при нажатии переход на регистарцию
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    //Проверка авторизации
    private fun validateLoginDetails(): Boolean {
        return when {
            //Проверка почты , не пустая ли строчка , если нет , то false . если да(пусто) то покажи ошибку
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            //так же проверка
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Вы успешно авторизовались!", false)
                true
            }
        }
    }
    //вход
    private fun logInRegisteredUser() {
        //Проверка целостности авторизации , заполнены ли все строчик , если true то норм)
        if (validateLoginDetails()) {

            //Загрузка
            showProgressDialog(resources.getString(R.string.please_wait))

            //Берем текст введеный пользователем в строку email и пароль
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            // Вход используя firebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    //закрываем progress dialog
                    hideProgressDialog()

                    if (task.isSuccessful) {

                        showErrorSnackBar("Вы успешно авторизовались!", false)
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

}