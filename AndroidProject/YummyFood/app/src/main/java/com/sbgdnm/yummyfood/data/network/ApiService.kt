package com.sbgdnm.yummyfood.data.network

import com.sbgdnm.yummyfood.data.network.responses.RecipeResponse
import com.sbgdnm.yummyfood.data.network.responses.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/get")
    suspend fun getRecipe(
        @Query("key") key: String,
        @Query("rId") recipeId: String
    ): RecipeResponse

    @GET("api/search")
    suspend fun searchRecipes(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("page") page: String
    ): RecipeSearchResponse
}
