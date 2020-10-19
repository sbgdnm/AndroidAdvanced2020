package com.sbgdnm.yummyfood.data.repository

import androidx.lifecycle.LiveData
import com.sbgdnm.yummyfood.data.model.Recipe
import com.sbgdnm.yummyfood.data.network.ApiClient
import com.sbgdnm.yummyfood.data.network.ApiService
import com.sbgdnm.yummyfood.data.network.RetrofitClient
import com.sbgdnm.yummyfood.data.network.responses.RecipeResponse
import com.sbgdnm.yummyfood.data.network.responses.RecipeSearchResponse
import com.sbgdnm.yummyfood.util.AppContants
import kotlin.properties.Delegates

class RecipeRepository {

    private val apiClient: ApiClient = ApiClient.getInstance()
    private lateinit var mQuery: String
    private var mPageNumber by Delegates.notNull<Int>()

    var apiService: ApiService = RetrofitClient.retrofit

    fun getRecipes(): LiveData<List<Recipe>> {
        return apiClient.mRecipes
    }

    suspend fun getRecipe(
        recipeId: String
    ): RecipeResponse {
        return apiService.getRecipe(AppContants.API_KEY, recipeId)
    }

    suspend fun searchRecipes(
        query: String,
        page: Int
    ): RecipeSearchResponse {
        this.mQuery = query
        this.mPageNumber = page
        return apiService.searchRecipes(AppContants.API_KEY, query, page.toString())
    }

    suspend fun searchNextPage(): RecipeSearchResponse {
        return searchRecipes(mQuery, mPageNumber + 1)
    }

    companion object {
        private var INSTANCE: RecipeRepository? = null
        fun getInstance() = INSTANCE
            ?: RecipeRepository().also {
                INSTANCE = it
            }
    }
}