package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.AddressWithPath
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class GetAddressByGeoCodeUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun getAddress(pointLatLng: String): AddressWithPath {
        return repository.getAddressUseCase(pointLatLng)
    }
}