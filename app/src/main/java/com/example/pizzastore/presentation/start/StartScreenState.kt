package com.example.pizzastore.presentation.start

import com.example.pizzastore.domain.entity.City

sealed class StartScreenState() {

    object Initial : StartScreenState()

    object StartScreenContent : StartScreenState()
}
