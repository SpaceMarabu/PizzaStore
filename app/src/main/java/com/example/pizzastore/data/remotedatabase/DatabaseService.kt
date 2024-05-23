package com.example.pizzastore.data.remotedatabase

import android.net.Uri
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface DatabaseService {

    fun getListStoriesUri(): SharedFlow<List<Uri>>

    fun getListCitiesFlow(): Flow<List<City>>

    fun getListProductsFlow(): Flow<List<Product>>
}