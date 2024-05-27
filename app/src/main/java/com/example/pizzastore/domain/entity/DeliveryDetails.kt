package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryDetails(
    val type: DeliveryType = DeliveryType.TAKE_OUT,
    val pizzaStore: Point? = null,
    val deliveryAddress: AddressDetails? = null,
    val deliveryGeoPoint: String? = null
) : Parcelable
