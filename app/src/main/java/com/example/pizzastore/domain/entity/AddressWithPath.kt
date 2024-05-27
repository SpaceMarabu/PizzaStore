package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressWithPath(
    val city: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val path: Path? = null
) : Parcelable {

    companion object {
        val EMPTY_ADDRESS = AddressWithPath()
    }
}