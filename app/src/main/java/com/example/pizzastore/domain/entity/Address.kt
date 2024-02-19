package com.example.pizzastore.domain.entity

data class Address(
    val city: String,
    val street: String,
    val houseNumber: String?,
    val path: Path? = null
)