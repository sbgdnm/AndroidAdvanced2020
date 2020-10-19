package com.sbgdnm.yummyfood.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.data.model.Recipe
import com.sbgdnm.yummyfood.util.AppContants
import com.sbgdnm.yummyfood.util.Resource
import com.sbgdnm.yummyfood.viewmodels.RecipeViewModel
import kotlin.math.roundToInt

class RecipeActivity : BaseActivity() {

    private val TAG = "RecipeActivity"

    private lateinit var mRecipeImageView: AppCompatImageView
    private lateinit var titleView: TextView
    private lateinit var rankView: TextView
    private lateinit var ingrediantContains: LinearLayout
    private lateinit var mScrollView: ScrollView

    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        mRecipeImageView = findViewById(R.id.recipe_image)
        titleView = findViewById(R.id.recipe_title)
        rankView = findViewById(R.id.recipe_social_score)
        ingrediantContains = findViewById(R.id.ingredients_container)
        mScrollView = findViewById(R.id.parent)

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        incomingIntent()
    }

    private fun incomingIntent() {
        if (intent.hasExtra(AppContants.RECIPE)) {
            val recipe: Recipe? = intent.getParcelableExtra(AppContants.RECIPE)
            loadData(recipe!!.recipe_id)
        }
    }

    private fun loadData(recipeId: String) {
        recipeViewModel.getRecipe(recipeId).observe(this, Observer {
            when (it) {
                is Resource.Success -> it.data?.let {
                    Log.d(TAG, "loadData: ${it.ingredients}")
                    mScrollView.visibility = View.VISIBLE
                    titleView.text = it.title
                    rankView.text = (it.social_rank.roundToInt()).toString()

                    ingrediantContains.removeAllViews()
                    for (ingredient in it.ingredients!!) {
                        val textView = TextView(this)
                        textView.text = ingredient
                        textView.textSize = 15F
                        textView.layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        ingrediantContains.addView(textView)
                    }

                    val requestOptions: RequestOptions =
                        RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_background)

                    Glide.with(this).setDefaultRequestOptions(requestOptions)
                        .load(it.image_url)
                        .into(mRecipeImageView)
                }
                is Resource.Error -> Log.d(TAG, "loadData error: ${it.message}")
            }
        })
    }
}