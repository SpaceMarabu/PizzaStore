package com.example.pizzastore.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import com.example.pizzastore.presentation.menu.MenuScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadCity()
        }
    }


    private suspend fun loadCity() {
        _state.emit(MainScreenState.Loading)
            getCurrentCityUseCase
            .getCurrentCityFlow()
            .collect {
                if (it == null) {
//                    _state.emit(MainScreenState.EmptyCity)
                } else {
                    _state.emit(MainScreenState.City)
                }
            }
    }

    fun changeState(state: MainScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}