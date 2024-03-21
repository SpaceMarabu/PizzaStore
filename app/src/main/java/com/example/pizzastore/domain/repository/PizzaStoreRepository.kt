package com.example.pizzastore.domain.repository

import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.SessionSettings
import kotlinx.coroutines.flow.Flow

interface PizzaStoreRepository {

    fun getCitiesUseCase(): Flow<List<City>>

    fun getCurrentSettingsUseCase(): Flow<SessionSettings?>

    suspend fun getAddressUseCase(pointLatLng: String): Address

    suspend fun getPathUseCase(point1: String, point2: String): Path

    suspend fun setCityUseCase(city: City)
}