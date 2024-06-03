package com.example.pizzastore.data.localdatabase

import androidx.room.Entity
import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.entity.OrdersHistory

@Entity(tableName = "account")
data class AccountDbModel(
    val id: Int = -1,
    val number: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val deliveryDetails: DeliveryDetails = DeliveryDetails(),
    val orders: OrdersHistory = OrdersHistory()
)
