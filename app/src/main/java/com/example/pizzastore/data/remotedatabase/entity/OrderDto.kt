package com.example.pizzastore.data.remotedatabase.entity

data class OrderDto(
    val id: Int,
    val status: String,
    val bucket: BucketDto
)
