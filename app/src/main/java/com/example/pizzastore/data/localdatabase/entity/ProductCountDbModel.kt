package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductCountDbModel(
    val idProduct: Int,
    val productCount: Int
) : Parcelable
