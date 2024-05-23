package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class DecreaseProductInBucketUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun decreaseProduct(product: Product) {
        return repository.decreaseProductInBucketUseCase(product)
    }
}