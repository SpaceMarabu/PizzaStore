package com.example.pizzastore.presentation.order.orderstatus

import com.example.pizzastore.domain.entity.Order

sealed class OrderStatusScreenState() {

    data object Initial : OrderStatusScreenState()

    data class Content(
        val order: Order
    ) : OrderStatusScreenState()

    data object EmptyOrder : OrderStatusScreenState()
}
