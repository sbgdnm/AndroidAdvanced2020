package com.sbgdnm.yummyfood.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.ui.activities.DashboardActivity
import kotlinx.android.synthetic.main.activity_splash.*

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Для того чтобы активити занимал весь экран но у меня что то не работе
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //Для задержки splash activity на 2.5секунд далее переход
        Handler().postDelayed(
            {
                // Launch the Login Activity
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish() // Call this when your activity is done and should be closed.
            },
            2500
        )
    }
}