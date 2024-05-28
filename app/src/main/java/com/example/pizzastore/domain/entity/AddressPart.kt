package com.example.pizzastore.domain.entity

import androidx.compose.runtime.Immutable


sealed class AddressPart() {

    @Immutable
    data class AddressLine(val address: String? = null) : AddressPart()
    @Immutable
    data class Entrance(val entrance: String? = null) : AddressPart()
    @Immutable
    data class DoorCode(val doorCode: String? = null) : AddressPart()
    @Immutable
    data class Floor(val floor: String? = null) : AddressPart()
    @Immutable
    data class Apartment(val apartment: String? = null) : AddressPart()
    @Immutable
    data class Comment(val comment: String? = null) : AddressPart()
}
