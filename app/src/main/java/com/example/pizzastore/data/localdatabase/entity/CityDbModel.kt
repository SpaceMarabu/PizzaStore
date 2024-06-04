package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import com.example.pizzastore.domain.entity.Point
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityDbModel(
    val id: Int = -1,
    val name: String = "Москва",
    val deliveryType: String,
    val points: List<PointDbModel> = listOf()
) : Parcelable