package com.example.pizzastore.presentation.main

import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.presentation.menu.MenuScreenState

sealed class MainScreenState() {

    object Initial : MainScreenState()
    object Loading : MainScreenState()


//    object EmptyCity : MainScreenState()

    object City : MainScreenState()

}
