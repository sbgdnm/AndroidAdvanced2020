package com.sbgdnm.yummyfood.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Product(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val ingredients: String = "",
    val image: String = "",
    var product_id: String = "",
) : Parcelable