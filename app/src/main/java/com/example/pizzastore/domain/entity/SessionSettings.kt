package com.example.pizzastore.domain.entity

data class SessionSettings(
    val city: City? = null,
    val address: AddressSealed.DeliveryInfo? = null,
    val account: Account? = null
)
