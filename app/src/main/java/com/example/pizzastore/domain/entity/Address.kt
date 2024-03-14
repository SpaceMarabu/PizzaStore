package com.example.pizzastore.domain.entity

data class Address(
    val city: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val path: Path? = null
) {

    companion object {
        val EMPTY_ADDRESS = Address()
    }
}