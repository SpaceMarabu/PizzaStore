package com.example.pizzastore.data.network.model

import com.google.gson.annotations.SerializedName

data class AddressDto(
    @SerializedName("city")
    val city: String,
    @SerializedName("street")
    val street: String,
    @SerializedName("housenumber")
    val houseNumber: String
)
