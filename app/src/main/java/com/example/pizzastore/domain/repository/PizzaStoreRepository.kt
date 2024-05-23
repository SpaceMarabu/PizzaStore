package com.example.pizzastore.domain.repository

import android.net.Uri
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import kotlinx.coroutines.flow.Flow

interface PizzaStoreRepository {

    fun getStoriesUseCase(): Flow<List<Uri>>

    fun getCitiesUseCase(): Flow<List<City>>

    fun getProductsUseCase(): Flow<List<Product>>

    fun getCurrentSettingsUseCase(): Flow<SessionSettings?>

    suspend fun getAddressUseCase(pointLatLng: String): Address

    suspend fun getPathUseCase(point1: String, point2: String): Path

    suspend fun setCityUseCase(city: City)

    suspend fun setPointUseCase(point: Point)
}