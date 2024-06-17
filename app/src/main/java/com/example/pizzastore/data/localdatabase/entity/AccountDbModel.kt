package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class AccountDbModel(
    val id: Int = -1,
    val number: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val deliveryDetails: DeliveryDetailsDbModel = DeliveryDetailsDbModel(),
    val orders: List<OrderDbModel> = listOf()
) : Parcelable
