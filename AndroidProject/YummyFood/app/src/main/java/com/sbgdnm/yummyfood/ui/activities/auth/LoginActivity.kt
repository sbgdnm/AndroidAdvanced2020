package com.sbgdnm.yummyfood.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.ui.activities.BaseActivity
import com.sbgdnm.yummyfood.ui.activities.MainActivity
import com.sbgdnm.yummyfood.ui.activities.UserProfileActivity
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

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        //закрываем progress dialog
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
        //Функция для уведомления Пользователя об успешном входе в систему и получения сведений о пользователе из базы данных FireStore после аутентификации.
    fun userLoggedInSuccess(user: User) {

        // закрываем progress dialog
        hideProgressDialog()

        //Распечатайте сведения о пользователе в журнале на данный момент.
        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

            if (user.profileCompleted == 0) {
                // если правильно залогинились и пользователь зашел впервые то открываем ему  профиль пользователя чтобы он настроил его
                val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
                startActivity(intent)
            } else {
                //если пользователь еже заходил и натстроил свой профиль то просто перенаравляем пользователя на главный экран после входа в систему.
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
            finish()

    }

}