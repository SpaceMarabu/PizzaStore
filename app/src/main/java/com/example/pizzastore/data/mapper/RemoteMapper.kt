package com.example.pizzastore.data.mapper

import com.example.pizzastore.data.remotedatabase.entity.BucketDto
import com.example.pizzastore.data.remotedatabase.entity.OrderDto
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.OrderStatus
import com.example.pizzastore.domain.entity.Product
import javax.inject.Inject

class RemoteMapper @Inject constructor() {

    //<editor-fold desc="mapBucketToBucketDto">
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
    //</editor-fold>

    //<editor-fold desc="mapOrderToOrderDto">
    fun mapOrderToOrderDto(order: Order) = OrderDto(
        id = order.id,
        status = order.status.ordinal.toString(),
        bucket = mapBucketToBucketDto(order.bucket)
    )
    //</editor-fold>

    //<editor-fold desc="mapBucketDtoToEntity">
    private fun mapBucketDtoToEntity(bucket: BucketDto, products: List<Product>): Bucket {
        val orderResult = mutableMapOf<Product, Int>()
        bucket.order.forEach { currentPair ->
            val currentProduct = products.filter { it.id == currentPair.key.toInt() }
            if (currentProduct.isNotEmpty()) {
                orderResult[currentProduct.first()] = currentPair.value
            }
        }
        return Bucket(order = orderResult)
    }
    //</editor-fold>

    //<editor-fold desc="mapOrderDtoToEntity">
    fun mapOrderDtoToEntity(orderDto: OrderDto?, products: List<Product>): Order? {
        if (orderDto == null) {
            return null
        }
        val id = orderDto.id
        val status: OrderStatus = when (orderDto.status) {
            OrderStatus.NEW.ordinal.toString() -> OrderStatus.NEW
            OrderStatus.PROCESSING.ordinal.toString() -> OrderStatus.PROCESSING
            else -> OrderStatus.FINISH
        }
        val bucket = mapBucketDtoToEntity(orderDto.bucket, products)
        return Order(
            id = id,
            status = status,
            bucket = bucket
        )
    }
    //</editor-fold>
}
