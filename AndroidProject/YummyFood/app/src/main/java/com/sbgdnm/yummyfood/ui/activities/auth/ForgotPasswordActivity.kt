package com.sbgdnm.yummyfood.ui.activities.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setupActionBar()


        btn_submit.setOnClickListener {

            // Сперва получим почту из поля ввода
            val email: String = et_email_forgot_pw.text.toString().trim { it <= ' ' }

            // если электронная почта введена в пустом виде, то покажите сообщение об ошибке или же продолжите работу с реализованной функцией.
            if (email.isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            } else {
                // Показываем progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                // Этот фрагмент кода используется для отправки ссылки сброса пароля на электронный идентификатор пользователя, если он зарегистрирован.
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        // Убераем progress dialog
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            // всплывающее сообщение и заверашает действие "забыли пароль", чтобы вернуться на экран входа в систему.
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_success),
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }

    private fun setupActionBar() {//при входе в активити рядом с тулбаром появляется иконка(стрелка) назад
        setSupportActionBar(toolbar_forgot_password_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }


}