package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCurrentOrderUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getCurrentOrderFlow(): StateFlow<Order?> {
        return repository.getOrderUseCase()
    }
}