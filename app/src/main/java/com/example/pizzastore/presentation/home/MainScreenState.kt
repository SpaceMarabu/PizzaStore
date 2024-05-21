package com.example.pizzastore.presentation.home

sealed class MainScreenState() {

    data object Initial : MainScreenState()
    data object Loading : MainScreenState()


//    object EmptyCity : MainScreenState()

    data object Content : MainScreenState()

}
