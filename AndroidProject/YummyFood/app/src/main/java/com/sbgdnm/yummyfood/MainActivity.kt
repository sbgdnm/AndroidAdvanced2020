package com.sbgdnm.yummyfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

import com.sbgdnm.yummyfood.auth.LoginActivity
import com.sbgdnm.yummyfood.auth.SignUpActivity

import com.sbgdnm.yummyfood.ui.RecipeListActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private  var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        init()

    }

    private fun init() {

        if (auth.currentUser != null) {
            startActivity(Intent(this, RecipeListActivity::class.java))
            finish()
        } else {
            btn_sign_in.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            btn_signup.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        }
    }
}