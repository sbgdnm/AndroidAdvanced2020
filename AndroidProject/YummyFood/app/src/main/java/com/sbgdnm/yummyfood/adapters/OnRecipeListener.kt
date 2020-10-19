package com.sbgdnm.yummyfood.adapters

interface OnRecipeListener {
    fun onRecipeClick(position: Int)

    fun onCategoryClick(category: String?)
}