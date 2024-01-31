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
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.usecases.GetCitiesUseCase
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import com.example.pizzastore.presentation.menu.MenuScreenState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _currentPointState = MutableSharedFlow<Point>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val currentPointState
        get() = _currentPointState.asSharedFlow()

    fun getStartCoords():LatLng {
        val currentScreenState = screenState.value as MapScreenState.Content
        val coords = currentScreenState.city.points[0].coords
        return getLatLngCoords(coords)
    }

    fun getStartPoint(): Point {
        val currentScreenState = screenState.value as MapScreenState.Content
        return currentScreenState.city.points[0]
    }

    fun getLatLngCoords(coords: String):LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }


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



    fun changeState(point: Point) {
        viewModelScope.launch {
            _currentPointState.emit(point)
        }
    }
}