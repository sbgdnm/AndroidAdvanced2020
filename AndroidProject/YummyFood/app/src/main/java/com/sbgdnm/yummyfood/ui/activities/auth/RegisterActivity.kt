package com.sbgdnm.yummyfood.ui.activities.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_register.*

@Suppress("DEPRECATION")
class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Для того чтобы активити занимал весь экран но у меня что то не работе
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        tv_login.setOnClickListener {
            //при нажатии переход на логин активити т.е. назад
            onBackPressed()
        }

        setupActionBar()

        btn_register.setOnClickListener {
            registerUser()
        }
    }
    private fun setupActionBar() { //при входе в регистрацию рядом с тулбаром появляется иконка(стрелка) назад
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean { //Проверка целостности аккаунта при регистарции
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
//                showErrorSnackBar("Вы успешно зарегистрировались!", false)
              true
            }
        }
    }
    private fun registerUser() {
        //Сперва идет проверка целостности аккаунта , если validate true то все окей, создаем аккаунт
        if (validateRegisterDetails()) {
            //Показываем загрузку с текстом из values/string
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Создаем аккаунт с помощью почты и пароля
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        hideProgressDialog() //зыкрываем progress dialog
                        // Если регистрация прошла успешно
                        if (task.isSuccessful) {

                            // Firebase регистрирует юзера
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            showErrorSnackBar(
                                "Вы успешно зарегистрировались!. Ваш user id ${firebaseUser.uid}",
                                false
                            )
                            //Так как после регистрации , пользователь сразу заходит в firebase
                            //мы просто выходим из firebase и отправляем его на login screen чтоб он самостоятельно вашел
                            FirebaseAuth.getInstance().signOut()
                            finish()

                        } else {
                            //Если регистрация не прошла успешно, то появится сообщение об ошибке.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }
}
