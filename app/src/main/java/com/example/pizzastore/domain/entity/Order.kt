package com.example.pizzastore.domain.entity

data class Order(
    val id: Int,
    val status: OrderStatus,
    val bucket: Bucket
)
