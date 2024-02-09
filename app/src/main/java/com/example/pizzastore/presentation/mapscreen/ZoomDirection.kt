package com.example.pizzastore.presentation.mapscreen

sealed class ZoomDirection {

    object Plus: ZoomDirection()
    object Minus: ZoomDirection()
    object  Nothing: ZoomDirection()
}