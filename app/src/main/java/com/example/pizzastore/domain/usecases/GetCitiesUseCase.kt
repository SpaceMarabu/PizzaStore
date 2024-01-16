package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCitiesUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getCities(): List<City> {
        return repository.getCitiesUseCase()
    }
}