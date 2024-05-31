package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class FinishOrderingUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun finishOrdering() {
        return repository.finishOrderingUseCase()
    }
}