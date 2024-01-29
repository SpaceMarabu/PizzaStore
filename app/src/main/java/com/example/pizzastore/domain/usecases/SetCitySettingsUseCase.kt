package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class SetCitySettingsUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun setCity(city: City) {
        return repository.setCitySettingsUseCase(city)
    }
}