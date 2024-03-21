package com.example.pizzastore.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.usecases.GetCurrentSettingsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuScreenViewModel @Inject constructor(
    private val getCurrentSettingsUseCase: GetCurrentSettingsUseCase
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
        getCurrentSettingsUseCase
            .getCurrentSettingsFlow()
            .stateIn(scope)
            .collect {
                if (it?.city == null) {
                    screenState.emit(MenuScreenState.EmptyCity)
//                    delay(300)
//                    screenState.emit(MenuScreenState.Loading)
                } else {
                    val city = it.city
                    screenState.emit(MenuScreenState.Content(city))
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