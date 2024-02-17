package com.example.pizzastore.presentation.mapscreen.takeout

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point

sealed class TakeoutMapScreenState() {

    object Initial : TakeoutMapScreenState()
    object Loading : TakeoutMapScreenState()

    data class Content(val city: City, val currentPoint: Point) : TakeoutMapScreenState()

}
