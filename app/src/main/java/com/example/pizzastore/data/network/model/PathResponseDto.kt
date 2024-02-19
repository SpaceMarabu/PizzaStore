package com.example.pizzastore.data.network.model

import com.google.gson.annotations.SerializedName

data class PathResponseDto(
    @SerializedName("paths")
    val paths: List<PathDto>
)
