package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import com.example.pizzastore.domain.entity.OrderStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDbModel(
    val id: Int,
    val status: String,
    val bucket: BucketDbModel
) : Parcelable
