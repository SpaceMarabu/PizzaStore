package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPosition(
    val target: MapPoint = MapPoint(),
    val zoom: Float = 17.0f,
    val azimuth: Float = 150.0f,
    val tilt: Float =  30.0f
): Parcelable
