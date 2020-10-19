package com.sbgdnm.yummyfood.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sbgdnm.yummyfood.data.model.Recipe
import com.sbgdnm.yummyfood.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers

class RecipeListViewModel : ViewModel() {

    private val recipeRepository: RecipeRepository = RecipeRepository.getInstance()

    var isViewingRecipes: Boolean = false
    var isPerformingQuery: Boolean = false

    val mRecipe: LiveData<List<Recipe>>
        get() = recipeRepository.getRecipes()

    fun searchRecipes(
        query: String,
        page: Int
    ) = liveData(Dispatchers.IO) {
        isViewingRecipes = true
        val retriveRecipeAbstractMutableList = recipeRepository.searchRecipes(query, page)
        emit(retriveRecipeAbstractMutableList.recipes)
    }

    fun onBackPressed(): Boolean {
        if (isViewingRecipes) {
            isViewingRecipes = false
            return false
        }
        return true
    }

    fun searchNextPage() = liveData(Dispatchers.IO) {
        if (!isPerformingQuery && isViewingRecipes) {
            val recipeAbstractMutableList = recipeRepository.searchNextPage()
            emit(recipeAbstractMutableList.recipes)
        }
    }
}