package com.example.pizzastore.presentation.home

sealed class MainScreenState() {

    object Initial : MainScreenState()
    object Loading : MainScreenState()


//    object EmptyCity : MainScreenState()

    object City : MainScreenState()

}
