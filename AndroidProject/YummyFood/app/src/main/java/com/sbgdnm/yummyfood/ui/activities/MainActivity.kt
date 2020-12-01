package com.sbgdnm.yummyfood.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            //Создаем экземпляр  SharedPreferences
        val sharedPreferences =
            getSharedPreferences(Constants.MY_YF_PREFERENCES, Context.MODE_PRIVATE)//

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        // даем результат
        tv_main.text= "Привет! $username."

    }
}