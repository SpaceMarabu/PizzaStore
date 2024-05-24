package com.example.pizzastore.presentation.bucket

import com.example.pizzastore.domain.entity.Product

sealed class BucketScreenState() {

    data object Initial : BucketScreenState()

    data class Content(
        val productsList: List<Product> = listOf()
    ) : BucketScreenState()
}
