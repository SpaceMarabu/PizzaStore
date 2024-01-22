package com.example.pizzastore.presentation.main

import com.example.pizzastore.domain.entity.City

sealed class MainScreenState() {

    object Initial : MainScreenState()
    object Loading : MainScreenState()

    object City : MainScreenState()

}
