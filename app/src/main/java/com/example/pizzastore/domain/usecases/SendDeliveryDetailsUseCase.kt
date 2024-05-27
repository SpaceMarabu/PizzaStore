package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class SendDeliveryDetailsUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun sendDetails(details: DeliveryDetails) {
        return repository.sendDeliveryDetailsUseCase(details)
    }
}