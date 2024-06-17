package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getProductsFlow(): Flow<List<Product>> {
        return repository.getProductsUseCase()
    }
}