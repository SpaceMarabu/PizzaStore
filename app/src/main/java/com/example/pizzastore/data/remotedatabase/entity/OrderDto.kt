package com.example.pizzastore.data.remotedatabase.entity

import com.example.pizzastore.domain.entity.OrderStatus

data class OrderDto(
    val id: Int,
    val status: OrderStatus,
    val bucket: BucketDto
)