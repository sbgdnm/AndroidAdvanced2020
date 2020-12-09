package com.sbgdnm.yummyfood.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*


/**
 * Класс адаптера для списка элементов панели мониторинга.
 */
open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<DashboardProduct>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    /**
     * Связывает каждый элемент в ArrayList с представлением
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_dashboard_item_image)
            holder.itemView.tv_dashboard_item_title.text = model.title
            holder.itemView.tv_dashboard_item_price.text = "TNG ${model.price}"
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