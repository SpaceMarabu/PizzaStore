package com.example.pizzastore.presentation.mapscreen.delivery

sealed class DeliveryMapScreenState {

    data object Initial : DeliveryMapScreenState()
    data object Loading : DeliveryMapScreenState()

    data object Content : DeliveryMapScreenState()

}
