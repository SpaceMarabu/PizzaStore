package com.example.pizzastore.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuScreenViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<MenuScreenState>(MenuScreenState.Initial)

    private val scope = viewModelScope


    init {
        scope.launch {
            loadCity()
        }
    }


    private suspend fun loadCity() {
        screenState.value = MenuScreenState.Loading
        getCurrentCityUseCase
            .getCurrentCityFlow()
            .stateIn(scope)
            .collect {
                it
                if (it == null) {
                    screenState.emit(MenuScreenState.EmptyCity)
                } else {
                    screenState.emit(MenuScreenState.Content(it))
//                    cityState.emit(it)
                }
            }
    }


    fun changeCityFeature(type: DeliveryType) {
        scope.launch {
            if (screenState.value is MenuScreenState.Content) {
                val currentState = screenState.value as MenuScreenState.Content
                val currentCity = currentState.city
                screenState.emit(
                    currentState.copy(
                        city = currentCity.copy(deliveryType = type)
                    )
                )
            }

        }
    }
}