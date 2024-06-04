package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressDetailsDbModel(
    val address: String? = null,
    val entrance: String? = null,
    val doorCode: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val comment: String? = null
) : Parcelable