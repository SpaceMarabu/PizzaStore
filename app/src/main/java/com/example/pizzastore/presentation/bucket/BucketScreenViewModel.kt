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
    private fun takeProductsFromBucket(bucket: Bucket): List<Product> {
        val resultList = mutableListOf<Product>()
        listProductTypes.forEach { currentType ->
            val notEmptyProduct = bucket.order.filter {
                it.value > 0
            }
            val currentTypeProducts =
                notEmptyProduct.keys.filter { currentProduct ->
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
                val products = takeProductsFromBucket(it)
                screenState.value = BucketScreenState.Content(
                    productsList = products,
                    bucket = it
                )
            }
    }
    //</editor-fold>

    //<editor-fold desc="getOrderSum">
    fun getOrderSum(): Int {
        var orderSum = 0
        val currentScreenState = screenState.value
        if (currentScreenState is BucketScreenState.Content) {
            currentScreenState.bucket.order.forEach {
                orderSum += it.key.price * it.value
            }
        }
        return orderSum
    }
    //</editor-fold>

    //<editor-fold desc="getOrderCountProducts">
    fun getOrderCountProducts(): Int {
        val currentScreenState = screenState.value
        var resultCount = 0
        if (currentScreenState is BucketScreenState.Content) {
            resultCount = currentScreenState.bucket.order.filter { it.value > 0 }.size
        }
        return resultCount
    }
    //</editor-fold>

    //<editor-fold desc="getProductSum">
    fun getProductSum(product: Product): Int {
        val currentScreenState = screenState.value
        var resultSum = 0
        if (currentScreenState is BucketScreenState.Content) {
            val countProductInBucket = currentScreenState.bucket.order[product] ?: 0
            resultSum = product.price * countProductInBucket
        }
        return resultSum
    }
    //</editor-fold>

    //<editor-fold desc="getProductCount">
    fun getProductCount(product: Product): Int {
        val currentScreenState = screenState.value
        var currentCount = 0
        if (currentScreenState is BucketScreenState.Content) {
            currentCount = currentScreenState.bucket.order[product] ?: 0
        }
        return currentCount
    }
    //</editor-fold>
}