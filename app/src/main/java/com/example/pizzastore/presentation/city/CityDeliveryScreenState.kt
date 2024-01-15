package com.example.pizzastore.presentation.city

import com.example.pizzastore.domain.entity.City

sealed class CityDeliveryScreenState() {

    object Initial : CityDeliveryScreenState()

    data class ListCities(
        val cities: List<City>
    ): CityDeliveryScreenState()

    data class CityChecked(
        val city: City
    ) : CityDeliveryScreenState()

    data class DeliveryChecked(
        val city: City
    ) : CityDeliveryScreenState()
}
