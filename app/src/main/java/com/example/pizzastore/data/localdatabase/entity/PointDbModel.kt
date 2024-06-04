package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointDbModel(
    val id: Int = -1,
    val address: String = "ул. Воли",
    val coords: String = "1, 1"
): Parcelable