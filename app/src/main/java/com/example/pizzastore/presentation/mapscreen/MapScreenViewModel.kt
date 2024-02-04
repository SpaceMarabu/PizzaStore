package com.example.pizzastore.presentation.mapscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        viewModelScope.launch {
            loadCity()
        }
    }

    fun getStartPoint(): Point {
        val currentScreenState = screenState.value as MapScreenState.Content
        return currentScreenState.city.points[0]
    }

    fun getCameraPosition(point: Point): CameraPosition {
        return CameraPosition.fromLatLngZoom(
            getLatLngCoords(point.coords),
            14f
        )
    }

    fun getLatLngCoords(coords: String):LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }
    private suspend fun loadCity() {
        _screenState.emit(MapScreenState.Loading)
        getCurrentCityUseCase
            .getCurrentCityFlow()
            .collect {
                if (it == null) return@collect
                _screenState.emit(MapScreenState.Content(it, it.points[0]))
            }
    }



    fun changeScreenState(state: MapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }

    fun changePointState(point: Point) {
        viewModelScope.launch {
            _currentPointState.emit(point)
        }
    }

}