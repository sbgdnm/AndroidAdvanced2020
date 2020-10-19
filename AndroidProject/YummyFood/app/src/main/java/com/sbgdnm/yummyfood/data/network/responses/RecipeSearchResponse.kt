package com.sbgdnm.yummyfood.data.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sbgdnm.yummyfood.data.model.Recipe

data class RecipeSearchResponse (
    @SerializedName("count") @Expose var count: Int,
    @SerializedName("recipes") @Expose var recipes: List<Recipe>
)