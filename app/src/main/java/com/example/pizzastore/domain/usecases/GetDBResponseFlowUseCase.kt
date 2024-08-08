package com.example.pizzastore.domain.usecases

import com.example.pizzastore.data.remotedatabase.model.DBResponseOrder
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDBResponseFlowUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getFlow(): Flow<DBResponseOrder> {
        return repository.getDbResponseFlow()
    }
}