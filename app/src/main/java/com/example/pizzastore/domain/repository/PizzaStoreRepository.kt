package com.example.pizzastore.domain.repository

import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Path
import kotlinx.coroutines.flow.Flow

interface PizzaStoreRepository {

    fun getCitiesUseCase(): Flow<List<City>>

    fun getCurrentCityUseCase(): Flow<City?>

    suspend fun getAddressUseCase(pointLatLng: String): Address

    suspend fun getPathUseCase(point1: String, point2: String): Path

    suspend fun setCitySettingsUseCase(city: City)
}