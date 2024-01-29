package com.example.pizzastore.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuScreenViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<MenuScreenState>(MenuScreenState.Initial)

    val cityState = MutableStateFlow(City())


    init {
        viewModelScope.launch {
            loadCity()
        }
    }


    private suspend fun loadCity() {
        screenState.value = MenuScreenState.Loading
        getCurrentCityUseCase
            .getCurrentCityFlow()
            .collect {
                if (it == null) {
                    screenState.emit(MenuScreenState.EmptyCity)
                } else {
                    screenState.emit(MenuScreenState.Content)
                    cityState.emit(it)
                }
            }
    }


    fun changeCityFeature(type: DeliveryType) {
        val currentCityState = cityState.value
        viewModelScope.launch {
            cityState.emit(
                currentCityState.copy(
                    deliveryType = type
                )
            )
        }
    }
}