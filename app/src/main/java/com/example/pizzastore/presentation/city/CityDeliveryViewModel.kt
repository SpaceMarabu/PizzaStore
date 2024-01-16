package com.example.pizzastore.presentation.city

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class CityDeliveryViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CityDeliveryScreenState>(CityDeliveryScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadCities()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TEST_TEST", "DEAD")
    }

    private suspend fun loadCities() {
        _state.value = CityDeliveryScreenState.Loading
        repeat(3) {
            val listCities = getCitiesUseCase.getCities()
            if (listCities.isNotEmpty()) {
                _state.value = CityDeliveryScreenState.ListCities(listCities)
                return@repeat
            } else {
                delay(3000)
            }
        }

    }

    fun changeState(state: CityDeliveryScreenState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}