package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCitiesUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getCitiesFlow(): Flow<List<City>> {
        return repository.getCitiesUseCase()
    }
}