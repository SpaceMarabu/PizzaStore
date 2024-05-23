package com.example.pizzastore.presentation.menu

import android.net.Uri
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType

sealed class ScreenStateChanges {

    data class ChangeCity(val city: City) : ScreenStateChanges()

    data class ChangeProducts(val products: List<Product>) : ScreenStateChanges()

    data class ChangeStories(val stories: List<Uri>) : ScreenStateChanges()

    data class ChangeIndexingMap(val map: Map<ProductType, List<Int>>) : ScreenStateChanges()
}
