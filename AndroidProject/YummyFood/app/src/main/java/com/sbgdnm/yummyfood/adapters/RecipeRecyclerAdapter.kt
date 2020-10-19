package com.sbgdnm.yummyfood.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sbgdnm.yummyfood.R

import com.sbgdnm.yummyfood.data.model.Recipe
import com.sbgdnm.yummyfood.util.AppContants
import kotlin.math.roundToInt

class RecipeRecyclerAdapter(
    onRecipeListener: OnRecipeListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RECIPE_TYPE = 1
    private val LOADING_TYPE = 2
    private val CATEGORY_TYPE = 3

    private var mRecipeList: List<Recipe> = ArrayList()
    private var mOnRecipeListener: OnRecipeListener = onRecipeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View?
        when (viewType) {
            RECIPE_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recipe_list_item, parent, false)
                return RecipeViewHolder(view, mOnRecipeListener)
            }

            LOADING_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_loading_list_item, parent, false)
                return LoadingViewHolder(view)
            }

            CATEGORY_TYPE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_category_list_item, parent, false)
                return CategoryViewHolder(view, mOnRecipeListener)
            }

            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recipe_list_item, parent, false)
                return RecipeViewHolder(view, mOnRecipeListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return mRecipeList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemType = getItemViewType(position)

        if (itemType == RECIPE_TYPE) {
            val requestOptions: RequestOptions =
                RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_background)

            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOptions)
                .load(mRecipeList[position].image_url)
                .into((holder as RecipeViewHolder).image)

            holder.title.text = mRecipeList[position].title
            holder.publisher.text = mRecipeList[position].publisher
            holder.socialScore.text =
                mRecipeList[position].social_rank.roundToInt().toString()
        } else if (itemType == CATEGORY_TYPE) {
            val requestOptions: RequestOptions =
                RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_background)

            val path: Uri =
                Uri.parse("android.resource://com.stv.foodrecipesapp/drawable/${mRecipeList[position].image_url}")
            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOptions)
                .load(path)
                .into((holder as CategoryViewHolder).imageView)

            holder.titleView.text = mRecipeList[position].title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            mRecipeList[position].social_rank.toInt() == -1 -> {
                CATEGORY_TYPE
            }
            mRecipeList[position].title == AppContants.LOADING_STRING -> {
                LOADING_TYPE
            }
            (position == (mRecipeList.size - 1)
                    && position != 0) -> {
                LOADING_TYPE
            }
            else -> {
                RECIPE_TYPE
            }
        }
    }

    fun displayLoading() {
        if (!isLoading()) {
            val recipe = Recipe(AppContants.LOADING_STRING, "", arrayOf(), "", "", 0F)
            val loadingList: MutableList<Recipe> = ArrayList()
            loadingList.add(recipe)
            mRecipeList = loadingList
            notifyDataSetChanged()
        }
    }

    private fun isLoading(): Boolean {
        if (mRecipeList.isNotEmpty()) {
            if (mRecipeList[mRecipeList.size - 1].title == AppContants.LOADING_STRING) {
                return true
            }
        }
        return false
    }

    fun displaySearchCategories() {
        val categoryList: MutableList<Recipe> = ArrayList()
        for (i in AppContants.DEFAULT_SEARCH_CATEGORIES.indices) {
            val recipe = Recipe(
                AppContants.DEFAULT_SEARCH_CATEGORIES[i],
                "",
                arrayOf(),
                "",
                AppContants.DEFAULT_SEARCH_CATEGORY_IMAGES[i],
                -1F
            )
            categoryList.add(recipe)
        }
        setRecipesList(categoryList)

    }

    fun setRecipesList(list: List<Recipe>) {
        mRecipeList = list
        notifyDataSetChanged()
    }

    fun selectedRecipe(position: Int): Recipe? {
        if (mRecipeList.isNotEmpty()) {
            return mRecipeList[position]
        }
        return null
    }
}