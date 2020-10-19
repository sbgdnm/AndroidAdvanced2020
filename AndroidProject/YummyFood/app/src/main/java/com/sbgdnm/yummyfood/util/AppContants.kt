package com.sbgdnm.yummyfood.util

class AppContants {
    companion object{
        const val BASE_URL = "https://recipesapi.herokuapp.com"
        const val API_KEY = ""
        const val REQUEST_TIMEOUT_DURATION: Long = 3000
        const val DEBUG = true
        const val LOADING_STRING = "LOADING..."

        const val RECIPE = "recipe"

        val DEFAULT_SEARCH_CATEGORIES = arrayOf(
            "Breakfast",
            "Chicken",
            "Beef",
            "Brunch",
            "Dinner",
            "Wine",
            "Barbeque",
            "Italian"
        )

        val DEFAULT_SEARCH_CATEGORY_IMAGES = arrayOf(
            "breakfast",
            "chicken",
            "beef",
            "brunch",
            "dinner",
            "wine",
            "barbeque",
            "italian"
        )
    }
}