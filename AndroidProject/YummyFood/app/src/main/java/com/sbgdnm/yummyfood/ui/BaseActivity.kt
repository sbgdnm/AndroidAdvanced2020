package com.sbgdnm.yummyfood.ui

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.sbgdnm.yummyfood.R

abstract class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    lateinit var progressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {
        val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout
        val frameLayout : FrameLayout = constraintLayout.findViewById(R.id.activity_content)
        progressBar = constraintLayout.findViewById(R.id.progress_bar)

        layoutInflater.inflate(layoutResID, frameLayout, true)

        super.setContentView(constraintLayout)
    }

    fun showProgressbar(visibility: Boolean) {

        Log.d(TAG, "showProgressbar: ... $visibility")
        progressBar.visibility = if (visibility) {
            View.VISIBLE
        }else{
            View.GONE
        }
    }
}