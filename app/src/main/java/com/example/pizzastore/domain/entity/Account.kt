package com.example.pizzastore.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val id: Int = -1,
    val number: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val deliveryDetails: DeliveryDetails = DeliveryDetails(),
    val orders: List<Order> = listOf()
) : Parcelable
