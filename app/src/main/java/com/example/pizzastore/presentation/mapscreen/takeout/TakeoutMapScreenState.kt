package com.example.pizzastore.presentation.mapscreen.takeout

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point

sealed class TakeoutMapScreenState {

    data object Initial : TakeoutMapScreenState()
    data object Loading : TakeoutMapScreenState()

    data class Content(val city: City, val currentPoint: Point) : TakeoutMapScreenState()

}
