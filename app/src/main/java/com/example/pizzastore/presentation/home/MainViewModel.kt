package com.example.pizzastore.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.usecases.GetCurrentSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getCurrentSettingsUseCase: GetCurrentSettingsUseCase
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
        getCurrentSettingsUseCase
            .getCurrentSettingsFlow()
            .collect {
                _state.emit(MainScreenState.Content)
            }
    }

    fun changeState(state: MainScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}