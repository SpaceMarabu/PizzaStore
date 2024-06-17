package com.example.pizzastore.presentation.mapscreen.takeout

sealed class ZoomDirection {

    data object Plus: ZoomDirection()
    data object Minus: ZoomDirection()
    data object  Nothing: ZoomDirection()
}