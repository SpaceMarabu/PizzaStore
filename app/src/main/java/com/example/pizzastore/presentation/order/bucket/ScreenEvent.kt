package com.example.pizzastore.presentation.order.bucket

sealed interface ScreenEvent {

    data object ExitScreen : ScreenEvent

    data object ErrorRepositoryResponse: ScreenEvent
}