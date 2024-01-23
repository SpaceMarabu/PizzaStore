package com.example.pizzastore.domain.repository

import com.example.pizzastore.domain.entity.City
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PizzaStoreRepository {

    fun getCitiesUseCase(): Flow<List<City>>

    fun getCurrentCityUseCase(): Flow<City?>

    suspend fun setCitySettings(city: City)
}