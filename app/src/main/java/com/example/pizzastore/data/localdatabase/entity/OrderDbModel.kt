package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDbModel(
    val id: Int,
    val status: String,
    val bucket: BucketDbModel
) : Parcelable
