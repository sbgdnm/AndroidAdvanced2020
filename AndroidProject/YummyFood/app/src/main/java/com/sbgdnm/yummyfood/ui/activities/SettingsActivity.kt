package com.sbgdnm.yummyfood.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.ui.activities.auth.LoginActivity
import com.sbgdnm.yummyfood.ui.activities.auth.UserProfileActivity
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() , View.OnClickListener{

    // Переменная для сведений о пользователе, которая будет инициализирована позже.
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar()

        //Назначьте событие onclick редактируемому тексту
        tv_edit.setOnClickListener(this@SettingsActivity)
        // Назначить событие onclick для кнопки "Выход".
        btn_logout.setOnClickListener(this@SettingsActivity)
        //сабытие для адресса
        ll_address.setOnClickListener(this@SettingsActivity)
    }
    //Переопределите функцию onResume и вызовите функцию getUserDetails init.
    override fun onResume() {
        super.onResume()

        getUserDetails()
    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * Функция для получения сведений о пользователе из firestore.
     */
    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        // Вызовите функцию класса Firestore, чтобы получить сведения о пользователе из уже созданного firestore.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    /**
     * Функция для получения сведений о пользователе и заполнения их в пользовательском интерфейсе.
     */
    fun userDetailsSuccess(user: User) {
        //Инициализируйте переменную mUserDetails.
        mUserDetails = user
        //Задать параметры пользователя для пользовательского интерфейса.
        // Hide the progress dialog
        hideProgressDialog()

        // Загрузите изображение с помощью класса Glide Loader.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // Вызовите действие профиля пользователя, чтобы добавить в приложение функцию редактирования профиля. Передайте данные пользователя через intent.
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                //Добавьте функцию выхода из системы, когда пользователь нажимает на кнопку выхода.
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                //вызоа списка адрессов
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }


}