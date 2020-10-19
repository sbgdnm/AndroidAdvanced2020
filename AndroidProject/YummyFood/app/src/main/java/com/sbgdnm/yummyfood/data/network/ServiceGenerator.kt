package com.sbgdnm.yummyfood.data.network

import com.stv.foodrecipesapp.util.AppContants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(AppContants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private val retrofit = retrofitBuilder.build()
    val recipeApi: ApiService = retrofit.create(ApiService::class.java)
}