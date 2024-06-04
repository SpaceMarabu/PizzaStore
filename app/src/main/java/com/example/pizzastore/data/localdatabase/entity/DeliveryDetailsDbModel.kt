package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryDetailsDbModel(
    val type: String = "0",
    val pizzaStore: PointDbModel? = null,
    val deliveryAddress: AddressDetailsDbModel? = null,
    val deliveryGeoPoint: String? = null
) : Parcelable
