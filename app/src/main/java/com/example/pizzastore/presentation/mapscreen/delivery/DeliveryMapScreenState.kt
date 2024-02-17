package com.example.pizzastore.presentation.mapscreen.delivery

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point

sealed class DeliveryMapScreenState() {

    object Initial : DeliveryMapScreenState()
    object Loading : DeliveryMapScreenState()

    object Content : DeliveryMapScreenState()

}
