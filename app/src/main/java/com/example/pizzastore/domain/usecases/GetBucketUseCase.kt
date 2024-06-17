package com.example.pizzastore.domain.usecases

import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetBucketUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getBucketFlow(): StateFlow<Bucket> {
        return repository.getBucketUseCase()
    }
}