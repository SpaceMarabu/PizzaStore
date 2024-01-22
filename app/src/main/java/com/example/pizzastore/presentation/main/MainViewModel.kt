package com.example.pizzastore.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Initial)
    val state = _state.asStateFlow()

    private lateinit var _defaultCity: City
    val defaultCity
        get() = _defaultCity

    init {
        viewModelScope.launch {
            loadCities()
        }
    }


    private suspend fun loadCities() {
        _state.value = MainScreenState.Loading
        delay(3000)
        getCitiesUseCase
            .getCitiesFlow()
            .filter { it.isNotEmpty() }
            .collect {
                _defaultCity = it[0]
                _state.value = MainScreenState.City
            }
    }

    fun changeState(state: MainScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}