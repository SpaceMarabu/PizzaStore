package com.example.pizzastore.presentation.start

sealed class StartScreenState {

    data object Initial : StartScreenState()

    data object StartScreenContent : StartScreenState()
}
