package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSettingsUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getCurrentSettingsFlow(): Flow<SessionSettings?> {
        return repository.getCurrentSettingsUseCase()
    }
}