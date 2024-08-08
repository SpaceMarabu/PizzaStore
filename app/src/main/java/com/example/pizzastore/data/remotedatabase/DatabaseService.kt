package com.example.pizzastore.data.remotedatabase

import android.net.Uri
import com.example.pizzastore.data.remotedatabase.model.BucketDto
import com.example.pizzastore.data.remotedatabase.model.DBResponseOrder
import com.example.pizzastore.data.remotedatabase.model.OrderDto
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface DatabaseService {

    fun getListStoriesUri(): SharedFlow<List<Uri>>

    fun getListCitiesFlow(): Flow<List<City>>

    fun getListProductsFlow(): Flow<List<Product>>

    suspend fun sendCurrentOrder(bucket: BucketDto): DBResponseOrder

    fun getCurrentOrder(): StateFlow<OrderDto?>

    fun sendLastOpenedOrderId(orderId: Int)

    fun acceptOrder()

    fun onOrderFinished()
}