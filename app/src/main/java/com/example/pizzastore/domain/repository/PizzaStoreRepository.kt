package com.example.pizzastore.domain.repository

import com.example.pizzastore.domain.entity.City
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PizzaStoreRepository {

    suspend fun getCitiesUseCase(): Flow<List<City>>

}