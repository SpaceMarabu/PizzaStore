package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class DisposeDbResponseUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun dispose() {
        return repository.disposeDBResponseUseCase()
    }
}