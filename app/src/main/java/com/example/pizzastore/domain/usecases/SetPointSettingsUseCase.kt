package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import javax.inject.Inject

class SetPointSettingsUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    suspend fun setPoint(point: Point) {
        return repository.setPointUseCase(point)
    }
}