package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BucketDbModel(
    val order: List<ProductCountDbModel> = listOf()
): Parcelable
