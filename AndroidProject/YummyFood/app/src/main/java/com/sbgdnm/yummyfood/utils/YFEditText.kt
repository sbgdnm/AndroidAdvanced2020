package com.myshoppal.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

class YFEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        //вызов функии
        applyFont()
    }
    private fun applyFont() {
        //Даем ширфт
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}