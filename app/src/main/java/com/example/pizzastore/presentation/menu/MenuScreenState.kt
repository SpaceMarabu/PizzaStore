package com.example.pizzastore.presentation.menu

import android.net.Uri
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType

sealed class MenuScreenState() {

    object Initial : MenuScreenState()
    object Loading : MenuScreenState()

    object EmptyCity : MenuScreenState()

    data class Content(
        val city: City,
        val stories: List<Uri> = listOf(),
        val products: List<Product> = listOf(),
        val indexingByTypeMap: Map<ProductType, List<Int>> = mapOf()
    ) : MenuScreenState()

}
