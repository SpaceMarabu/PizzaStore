package com.example.pizzastore.domain.entity

import com.google.gson.annotations.SerializedName

data class Address(
    val city: String,
    val street: String,
    val houseNumber: String?
)