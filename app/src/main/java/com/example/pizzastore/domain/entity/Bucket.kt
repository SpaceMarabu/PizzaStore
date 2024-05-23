package com.example.pizzastore.domain.entity

data class Bucket(
    val order: Map<Product, Int> = mapOf()
)
