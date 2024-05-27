package com.example.pizzastore.domain.entity

data class AddressState (
    val address: AddressWithPath? = null,
    val isInputTextStarted: Boolean = false
)

