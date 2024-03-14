package com.example.pizzastore.domain.entity


sealed class AddressSealed() {
    data class DeliveryInfo(
        val address: String? = null,
        val entrance: String? = null,
        val doorCode: String? = null,
        val floor: String? = null,
        val apartment: String? = null,
        val comment: String? = null
    ) : AddressSealed()

    data class AddressLine(val address: String? = null) : AddressSealed()
    data class Entrance(val entrance: String? = null) : AddressSealed()
    data class DoorCode(val doorCode: String? = null) : AddressSealed()
    data class Floor(val floor: String? = null) : AddressSealed()
    data class Apartment(val apartment: String? = null) : AddressSealed()
    data class Comment(val comment: String? = null) : AddressSealed()
}
