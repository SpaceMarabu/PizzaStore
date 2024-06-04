package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentOrderUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getCurrentOrderFlow(): Flow<Order?> {
        return repository.getCurrentOrderUseCase()
    }
}