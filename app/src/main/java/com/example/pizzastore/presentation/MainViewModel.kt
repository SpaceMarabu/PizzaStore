package com.example.pizzastore.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow<CityScreenState>(CityScreenState.Initial)
    val state = _state.asStateFlow()

    fun changeState(state: CityScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}