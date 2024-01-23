package com.example.pizzastore.presentation.menu

import com.example.pizzastore.domain.entity.City

sealed class MenuScreenState() {

    object Initial : MenuScreenState()
    object Loading : MenuScreenState()

    object Content : MenuScreenState()

}
