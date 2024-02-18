package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAddressUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun getAddress(pointLatLng: String): Address {
        return repository.getAddressUseCase(pointLatLng)
    }
}