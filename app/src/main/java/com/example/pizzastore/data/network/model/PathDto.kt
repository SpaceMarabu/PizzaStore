package com.example.pizzastore.data.network.model

import com.google.gson.annotations.SerializedName

data class PathDto(
    @SerializedName("distance")
    val distance: Long,
    @SerializedName("time")
    val time: Long
)
