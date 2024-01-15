package com.example.pizzastore.presentation.main

import com.example.pizzastore.domain.entity.City

sealed class StartScreenState() {

    object Initial : StartScreenState()

    data class StartScreenContent(
        val city: City
    ) : StartScreenState()
}
