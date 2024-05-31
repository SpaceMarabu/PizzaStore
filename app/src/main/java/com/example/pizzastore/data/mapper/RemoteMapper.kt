package com.example.pizzastore.data.mapper

import com.example.pizzastore.data.remotedatabase.entity.BucketDto
import com.example.pizzastore.data.remotedatabase.entity.OrderDto
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.Order
import javax.inject.Inject

class RemoteMapper @Inject constructor() {

    fun mapBucketToBucketDto(bucket: Bucket): BucketDto {
        val productsFromBucket = bucket.order
        val mapProductsForDB = mutableMapOf<String, Int>()
        productsFromBucket.forEach {
            val productId = it.key.id.toString()
            val productCount = it.value
            mapProductsForDB[productId] = productCount
        }
        return BucketDto(order = mapProductsForDB)
    }

    fun mapOrderToOrderDto(order: Order) = OrderDto(
        id = order.id,
        status = order.status,
        bucket = mapBucketToBucketDto(order.bucket)
    )

}
