package com.example.pizzastore.presentation.city

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class CityDeliveryViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase
): ViewModel() {

    private val _state = MutableStateFlow<CityDeliveryScreenState>(CityDeliveryScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val citiesFlow = getCitiesUseCase.getCities().stateIn(this)
            citiesFlow
                .filter { it.isNotEmpty() }
                .collect {
                _state.emit(CityDeliveryScreenState.ListCities(it))
            }
        }
    }

    fun changeState(state: CityDeliveryScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}