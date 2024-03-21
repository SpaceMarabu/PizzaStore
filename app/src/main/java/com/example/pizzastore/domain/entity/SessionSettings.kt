package com.example.pizzastore.domain.entity

data class SessionSettings(
    val id: Int = 1,
    val city: City? = null,
    val account: Account? = null
)
