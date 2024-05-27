package com.example.pizzastore.domain.entity


sealed class AddressParts() {

    data class AddressLine(val address: String? = null) : AddressParts()
    data class Entrance(val entrance: String? = null) : AddressParts()
    data class DoorCode(val doorCode: String? = null) : AddressParts()
    data class Floor(val floor: String? = null) : AddressParts()
    data class Apartment(val apartment: String? = null) : AddressParts()
    data class Comment(val comment: String? = null) : AddressParts()
}
