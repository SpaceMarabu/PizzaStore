package com.example.pizzastore.presentation.mapscreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

class MapScreenViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow<MapScreenState>(MapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()


    init {
        viewModelScope.launch {
            loadCity()
        }
    }

    private suspend fun loadCity() {
        _screenState.emit(MapScreenState.Loading)
        getCurrentCityUseCase
            .getCurrentCityFlow()
            .collect {
                if (it == null) return@collect
                _screenState.emit(MapScreenState.Content(it))
            }
    }



//    fun changeState(state: MainScreenState) {
//        viewModelScope.launch {
//            _state.emit(state)
//        }
//    }
}