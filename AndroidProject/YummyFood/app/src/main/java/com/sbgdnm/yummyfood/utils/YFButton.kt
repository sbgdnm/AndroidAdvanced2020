package com.myshoppal.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class YFButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {
    init {
        //вызов функии
        applyFont()
    }

    private fun applyFont() {

        //Даем ширфт
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)

    }
}