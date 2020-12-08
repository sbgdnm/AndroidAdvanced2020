package com.sbgdnm.yummyfood.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.ui.activities.AddMyProductActivity
import com.sbgdnm.yummyfood.ui.activities.SettingsActivity


class ProductsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Если мы хотим использовать меню опций во фрагменте, нам нужно добавить его.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_products, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        textView.text = "Мои рецепты"

        return root
    }
    //Переопределите функцию onCreateOptionMenu и раздуйте файл меню панели мониторинга init.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_myproduct_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    //Переопределите функцию onOptionItemSelected и обработайте элементы действия init.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_add_myproduct -> {
                //Launch the AddMyProductActivity on click of action item.
                startActivity(Intent(activity, AddMyProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}