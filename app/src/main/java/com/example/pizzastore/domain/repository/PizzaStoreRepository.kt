package com.example.pizzastore.domain.repository

import com.example.pizzastore.domain.entity.City
import kotlinx.coroutines.flow.Flow

interface PizzaStoreRepository {

    fun getCitiesUseCase(): Flow<List<City>>

    fun getCurrentCityUseCase(): Flow<City?>

    suspend fun setCitySettingsUseCase(city: City)
}