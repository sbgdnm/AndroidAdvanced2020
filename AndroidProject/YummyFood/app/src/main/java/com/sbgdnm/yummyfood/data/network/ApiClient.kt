package com.sbgdnm.yummyfood.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sbgdnm.yummyfood.data.model.Recipe

class ApiClient {

    private val TAG = "ApiClient"

    private var _mRecipes: MutableLiveData<List<Recipe>> = MutableLiveData()


    val mRecipes: LiveData<List<Recipe>>
        get() = _mRecipes


    companion object {
        private var INSTANCE: ApiClient? = null
        fun getInstance() = INSTANCE
            ?: ApiClient().also {
                INSTANCE = it
            }
    }
}