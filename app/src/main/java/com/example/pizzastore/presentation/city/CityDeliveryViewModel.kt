package com.example.pizzastore.presentation.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class CityDeliveryViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CityDeliveryScreenState>(CityDeliveryScreenState.Initial)
    val state = _state.asStateFlow()

    private var _previousState: CityDeliveryScreenState = CityDeliveryScreenState.Initial
    val previousState
        get() = _previousState

    init {
        viewModelScope.launch {
            loadCities()
        }
    }


    private suspend fun loadCities() {
        _state.value = CityDeliveryScreenState.Loading
        getCitiesUseCase
            .getCitiesFlow()
            .filter { it.isNotEmpty() }
            .collect {
                _state.value = CityDeliveryScreenState.ListCities(it)
            }
    }

    fun changeState(state: CityDeliveryScreenState) {
        _previousState = _state.value
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}