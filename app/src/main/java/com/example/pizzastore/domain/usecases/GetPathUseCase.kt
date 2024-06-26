package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class GetPathUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun getPath(point1: String, point2: String): Path {
        return repository.getPathUseCase(point1, point2)
    }
}