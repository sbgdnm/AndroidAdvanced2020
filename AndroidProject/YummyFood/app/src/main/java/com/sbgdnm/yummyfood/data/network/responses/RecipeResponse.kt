package com.sbgdnm.yummyfood.data.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sbgdnm.yummyfood.data.model.Recipe

data class RecipeResponse(
    @SerializedName("recipe") @Expose var recipe: Recipe
)