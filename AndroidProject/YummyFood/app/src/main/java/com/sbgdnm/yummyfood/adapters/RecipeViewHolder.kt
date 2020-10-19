package com.sbgdnm.yummyfood.adapters

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.adapters.OnRecipeListener



class RecipeViewHolder(
    itemView: View,
    onRecipeListener: OnRecipeListener
) :
    ViewHolder(itemView), View.OnClickListener {

    var title: TextView = itemView.findViewById(R.id.recipe_title)
    var publisher: TextView = itemView.findViewById(R.id.recipe_publisher)
    var socialScore: TextView = itemView.findViewById(R.id.recipe_social_score)
    var image: AppCompatImageView = itemView.findViewById(R.id.recipe_image)
    var onRecipeListener: OnRecipeListener = onRecipeListener

    override fun onClick(v: View) {
        onRecipeListener.onRecipeClick(adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
    }
}