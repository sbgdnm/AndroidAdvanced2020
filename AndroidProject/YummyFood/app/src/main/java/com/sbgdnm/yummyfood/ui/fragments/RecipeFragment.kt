package com.sbgdnm.yummyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.ui.activities.AddMyProductActivity
import com.sbgdnm.yummyfood.ui.adapters.RecipeItemsListAdapter
import kotlinx.android.synthetic.main.fragment_recipe.*


class  RecipeFragment : BaseFragment() {

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

        val root = inflater.inflate(R.layout.fragment_recipe, container, false)
        return root
    }
    override fun onResume() {
        super.onResume()
        getRecipeItemsList()
    }


  // Функция для получения списка элементов панели мониторинга из cloud firestore.
    private fun getRecipeItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getRecipeItemsList(this@RecipeFragment)
    }

   //Функция, чтобы получить успешный результат от пользования рецепт от облака firestore.
    fun successRecipeItemsList(recipeItemsList: ArrayList<Product>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (recipeItemsList.size > 0) {

            rv_recipe_items.visibility = View.VISIBLE
            tv_no_recipe_items_found.visibility = View.GONE

            rv_recipe_items.layoutManager = LinearLayoutManager(activity) //LinearLayoutManager(activity) GridLayoutManager(activity, 2)
            rv_recipe_items.setHasFixedSize(true)

            val adapter = RecipeItemsListAdapter(requireActivity(), recipeItemsList)
            rv_recipe_items.adapter = adapter
        } else {
            rv_recipe_items.visibility = View.GONE
            tv_no_recipe_items_found.visibility = View.VISIBLE
        }
    }

}