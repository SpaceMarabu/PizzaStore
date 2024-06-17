package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class IncreaseProductInBucketUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun increaseProduct(product: Product) {
        return repository.increaseProductInBucketUseCase(product)
    }
}