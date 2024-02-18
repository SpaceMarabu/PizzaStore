package com.example.pizzastore.data.network.model

import com.google.gson.annotations.SerializedName

data class AddressResponseDto(
    @SerializedName("hits")
    val addressList: List<AddressDto>
)
