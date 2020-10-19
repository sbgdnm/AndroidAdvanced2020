package com.sbgdnm.yummyfood.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sbgdnm.yummyfood.data.repository.RecipeRepository
import com.stv.foodrecipesapp.util.Resource
import kotlinx.coroutines.Dispatchers

class RecipeViewModel : ViewModel() {

    private val recipeRepository: RecipeRepository = RecipeRepository.getInstance()
    var isViewingRecipePage: Boolean = false

    fun getRecipe(
        recipeId: String
    ) = liveData(Dispatchers.IO) {
        try {
            isViewingRecipePage = true
            val retriveRecipeResult = recipeRepository.getRecipe(recipeId)
            //emit(retriveRecipeResult.recipe)
            emit(Resource.Success(retriveRecipeResult.recipe))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString(), null))
        }
    }

    fun onBackPressed(): Boolean {
        if (isViewingRecipePage) {
            isViewingRecipePage = false
            return false
        }
        return true
    }
}