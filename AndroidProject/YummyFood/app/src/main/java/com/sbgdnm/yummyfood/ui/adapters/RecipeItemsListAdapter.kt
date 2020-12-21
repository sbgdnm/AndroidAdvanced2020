package com.sbgdnm.yummyfood.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.ui.activities.RecipeDetailsActivity
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.item_recipe_layout.view.*

/**
 *Класс адаптера для списка элементов рецепта.
 */
open class RecipeItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Раздувает представления элементов, которые разработаны в файле xml-макета
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_recipe_layout,
                parent,
                false
            )
        )
    }

    /**
     * Связывает каждый элемент в ArrayList с представлением
     *
     * Вызывается, когда RecyclerView нуждается в новом {@link ViewHolder} данного типа для представления элемента.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_recipe_item_image)
            holder.itemView.tv_recipe_item_title.text = model.title
            holder.itemView.tv_recipe_item_price.text = "TNG ${model.price}"
            holder.itemView.tv_recipe_item_name.text = "${model.user_name}"
            holder.itemView.tv_recipe_item_ingredients.text = "Ингридиенты: ${model.ingredients}..."

            holder.itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailsActivity::class.java)
                //Pass the product id to the product details screen through intent.
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                context.startActivity(intent)
            }
        }
    }

    /**
     *Возвращает количество элементов в списке
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * ViewHolder описывает представление элемента и метаданные о его месте в RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}