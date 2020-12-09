package com.sbgdnm.yummyfood.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.sbgdnm.yummyfood.R
import kotlinx.android.synthetic.main.dialog_progress.*
//Базовый класс фрагментов используется для определения функций и членов, которые мы будем использовать во всех фрагментах.
// Он наследует класс Fragment, поэтому в другом классе fragment мы заменим фрагмент на BaseFragment.
open class BaseFragment : Fragment() {

    //Это экземпляр диалога прогресса, который мы инициализируем позже.
    private lateinit var mProgressDialog: Dialog
    /**
     *Эта функция используется для отображения диалога прогресса с заголовком и сообщением пользователю.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * Эта функция используется для закрытия диалога выполнения, если он виден пользователю.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }


}