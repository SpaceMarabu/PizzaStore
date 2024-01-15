package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val id: Int,
    val name: String,
    val deliveryType: DeliveryType = DeliveryType.TAKE_OUT
) : Parcelable {

}