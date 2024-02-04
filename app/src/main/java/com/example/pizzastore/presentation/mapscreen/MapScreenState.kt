package com.example.pizzastore.presentation.mapscreen

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point

sealed class MapScreenState() {

    object Initial : MapScreenState()
    object Loading : MapScreenState()

    data class Content(val city: City, val currentPoint: Point) : MapScreenState()

}
