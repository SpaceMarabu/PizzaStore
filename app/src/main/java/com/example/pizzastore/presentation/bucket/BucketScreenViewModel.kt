package com.example.pizzastore.presentation.bucket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType
import com.example.pizzastore.domain.usecases.DecreaseProductInBucketUseCase
import com.example.pizzastore.domain.usecases.GetBucketUseCase
import com.example.pizzastore.domain.usecases.IncreaseProductInBucketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BucketScreenViewModel @Inject constructor(
    private val getBucketUseCase: GetBucketUseCase,
    private val increaseProductInBucketUseCase: IncreaseProductInBucketUseCase,
    private val decreaseProductInBucketUseCase: DecreaseProductInBucketUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<BucketScreenState>(BucketScreenState.Initial)
    private val bucket = MutableStateFlow(Bucket())

    private val listProductTypes = ProductType.allTypes

    init {
        viewModelScope.launch {
            subscribeBucket()
        }
    }

    //<editor-fold desc="increaseProductInBucket">
    fun increaseProductInBucket(product: Product) {
        increaseProductInBucketUseCase.increaseProduct(product)
    }
    //</editor-fold>

    //<editor-fold desc="decreaseProductInBucket">
    fun decreaseProductInBucket(product: Product) {
        decreaseProductInBucketUseCase.decreaseProduct(product)
    }
    //</editor-fold

    //<editor-fold desc="takeProductsFromBucket">
    fun takeProductsFromBucket(bucket: Bucket): List<Product> {
        val resultList = mutableListOf<Product>()
        listProductTypes.forEach { currentType ->
            val currentTypeProducts =
                bucket.order.keys.filter { currentProduct ->
                    currentProduct.type == currentType
                }
            resultList.addAll(currentTypeProducts)
        }
        return resultList
    }
    //</editor-fold>

    //<editor-fold desc="subscribeBucket">
    private suspend fun subscribeBucket() {
        getBucketUseCase
            .getBucketFlow()
            .collect {
                bucket.value = it
                val products = takeProductsFromBucket(it)
                screenState.value = BucketScreenState.Content(productsList = products)
            }
    }
    //</editor-fold>

    //<editor-fold desc="getOrderSum">
    fun getOrderSum(): Int {
        var orderSum = 0
        bucket.value.order.forEach {
            orderSum += it.key.price * it.value
        }
        return orderSum
    }
    //</editor-fold>

    //<editor-fold desc="getOrderCountProducts">
    fun getOrderCountProducts() = bucket.value.order.filter { it.value > 0 }.size
    //</editor-fold>

    //<editor-fold desc="getProductSum">
    fun getProductSum(product: Product): Int {
        val countProductInBucket = bucket.value.order[product] ?: 0
        return product.price * countProductInBucket
    }
    //</editor-fold>

    //<editor-fold desc="getProductCount">
    fun getProductCount(product: Product) = bucket.value.order[product] ?: 0
    //</editor-fold>
}