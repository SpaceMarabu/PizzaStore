package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressDetails(
    val address: String? = null,
    val entrance: String? = null,
    val doorCode: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val comment: String? = null
) : Parcelable