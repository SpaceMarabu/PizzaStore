package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentOrderIdUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

//    fun getCurrentOrderIdFlow(): Flow<Int> {
//        return repository.getCurrentOrderIdUseCase()
//    }
}