package com.example.pizzastore.data.remotedatabase.model

data class OrderDto(
    val id: Int,
    val status: String,
    val bucket: BucketDto
)
