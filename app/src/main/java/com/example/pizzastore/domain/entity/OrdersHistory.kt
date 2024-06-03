package com.example.pizzastore.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity()
data class OrdersHistory(
    val listOrders: List<Order> = listOf()
) : Parcelable
