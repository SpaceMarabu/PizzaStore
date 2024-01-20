package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPoint(
    val x: Double = 55.751225,
    val y: Double = 37.629540
): Parcelable
