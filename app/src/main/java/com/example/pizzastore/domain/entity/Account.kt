package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val id: Int = -1,
    val number: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val homeAddress: AddressResult.DeliveryInfo? = null,
    val pizzaStore: Point? = null
) : Parcelable
