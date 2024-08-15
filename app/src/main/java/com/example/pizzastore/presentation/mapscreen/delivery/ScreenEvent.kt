package com.example.pizzastore.presentation.mapscreen.delivery

sealed interface ScreenEvent {

    data object SaveClicked: ScreenEvent

    data object ExitScreen: ScreenEvent
}