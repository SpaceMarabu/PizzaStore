package com.example.pizzastore.presentation.mapscreen

import com.example.pizzastore.domain.entity.City

sealed class MapScreenState() {

    object Initial : MapScreenState()
    object Loading : MapScreenState()

    data class Content(val city: City) : MapScreenState()

}
