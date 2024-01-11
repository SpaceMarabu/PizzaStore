package com.example.pizzastore.presentation

import com.example.pizzastore.domain.City

sealed class CityScreenState() {

    object Initial : CityScreenState()

    data class CityContent(
        val city: City
    ) : CityScreenState()
}
