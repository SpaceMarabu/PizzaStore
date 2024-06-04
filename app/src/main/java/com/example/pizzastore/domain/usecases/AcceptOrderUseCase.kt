package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class AcceptOrderUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun acceptOrder() {
        return repository.acceptOrderUseCase()
    }
}