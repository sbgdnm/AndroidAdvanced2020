package com.sbgdnm.yummyfood.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.ui.activities.RecipeDetailsActivity
import com.sbgdnm.yummyfood.ui.fragments.ProductsFragment
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

/**
 * Класс адаптера для элементов списка продуктов.
 */
open class MyProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Раздувает представления элементов, которые разработаны в файле xml-макета
     *
     * создайте новый
     * {@link ViewHolder} и инициализирует некоторые частные поля для использования RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    /**
     * Связывает каждый элемент в ArrayList с представлением
     *
     * Вызывается, когда RecyclerView нуждается в новом {@link ViewHolder} данного типа для представления элемента.
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "TNG ${model.price}"

            //  Назначить событие click для кнопки "Удалить".
            holder.itemView.ib_delete_product.setOnClickListener {
                //Теперь давайте вызовем функцию delete ProductsFragment.
                fragment.deleteProduct(model.product_id)

            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailsActivity::class.java)
                //Pass the product id to the product details screen through intent.
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                context.startActivity(intent)
            }

        }
    }

    /**
     * Возвращает количество элементов в списке
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * ViewHolder описывает представление элемента и метаданные о его месте в RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}