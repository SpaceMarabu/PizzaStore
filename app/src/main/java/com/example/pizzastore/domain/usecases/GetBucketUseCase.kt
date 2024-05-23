package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetBucketUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getBucketFlow(): StateFlow<Bucket> {
        return repository.getBucketUseCase()
    }
}