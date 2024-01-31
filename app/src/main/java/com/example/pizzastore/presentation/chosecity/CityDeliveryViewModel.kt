package com.example.pizzastore.presentation.chosecity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import com.example.pizzastore.domain.usecases.SetCitySettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.io.Closeable
import javax.inject.Inject

class CityDeliveryViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val setCitySettingsUseCase: SetCitySettingsUseCase
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

    fun sendCity(city: City) {
        viewModelScope.launch {
            setCitySettingsUseCase.setCity(city)
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