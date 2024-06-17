package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class GetCurrentOrderIdUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

//    fun getCurrentOrderIdFlow(): Flow<Int> {
//        return repository.getCurrentOrderIdUseCase()
//    }
}