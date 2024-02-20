package com.example.pizzastore.data.network.model

import com.google.gson.annotations.SerializedName

data class PathDto(
    @SerializedName("distance")
    val distance: Float,
    @SerializedName("time")
    val time: Long
)
