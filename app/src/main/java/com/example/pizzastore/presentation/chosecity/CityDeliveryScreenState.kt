package com.example.pizzastore.presentation.chosecity

import com.example.pizzastore.domain.entity.City

sealed class CityDeliveryScreenState {

    data object Initial : CityDeliveryScreenState()
    data object Loading : CityDeliveryScreenState()

    data class ListCities(
        val cities: List<City>
    ): CityDeliveryScreenState()

    data class CityChecked(
        val city: City
    ) : CityDeliveryScreenState()

//    data class DeliveryChecked(
//        val city: City
//    ) : CityDeliveryScreenState()
}
