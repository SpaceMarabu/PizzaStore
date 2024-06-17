package com.example.pizzastore.domain.usecases

import android.net.Uri
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoriesUseCase  @Inject constructor(
    private val repository: PizzaStoreRepository
) {

    fun getStoriesFlow(): Flow<List<Uri>> {
        return repository.getStoriesUseCase()
    }
}