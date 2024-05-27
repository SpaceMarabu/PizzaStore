package com.example.pizzastore.domain.repository

import android.net.Uri
import com.example.pizzastore.domain.entity.AddressWithPath
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PizzaStoreRepository {

    fun getStoriesUseCase(): Flow<List<Uri>>

    fun getCitiesUseCase(): Flow<List<City>>

    fun getProductsUseCase(): Flow<List<Product>>

    fun getCurrentSettingsUseCase(): Flow<SessionSettings?>

    suspend fun getAddressUseCase(pointLatLng: String): AddressWithPath

    suspend fun getPathUseCase(point1: String, point2: String): Path

    suspend fun setCityUseCase(city: City)

    suspend fun setPointUseCase(point: Point)

    fun increaseProductInBucketUseCase(product: Product)

    fun decreaseProductInBucketUseCase(product: Product)

    fun getBucketUseCase(): StateFlow<Bucket>

    fun sendDeliveryDetailsUseCase(details: DeliveryDetails)

    fun finishOrderingUseCase()
}