package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class AddressResult() {

    @Parcelize
    data class DeliveryInfo(
        val address: String? = null,
        val entrance: String? = null,
        val doorCode: String? = null,
        val floor: String? = null,
        val apartment: String? = null,
        val comment: String? = null
    ) : AddressResult(), Parcelable

    data class AddressLine(val address: String? = null) : AddressResult()
    data class Entrance(val entrance: String? = null) : AddressResult()
    data class DoorCode(val doorCode: String? = null) : AddressResult()
    data class Floor(val floor: String? = null) : AddressResult()
    data class Apartment(val apartment: String? = null) : AddressResult()
    data class Comment(val comment: String? = null) : AddressResult()
}
