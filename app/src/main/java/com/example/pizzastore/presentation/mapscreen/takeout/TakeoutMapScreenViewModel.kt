package com.example.pizzastore.presentation.mapscreen.takeout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.usecases.GetCurrentSettingsUseCase
import com.example.pizzastore.domain.usecases.SetCitySettingsUseCase
import com.example.pizzastore.domain.usecases.SetPointSettingsUseCase
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TakeoutMapScreenViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentSettingsUseCase,
    private val setPointSettingsUseCase: SetPointSettingsUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow<TakeoutMapScreenState>(TakeoutMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()


    private lateinit var currentPoint: Point
    private var currentZoom = 14f
    private lateinit var currentCameraPosition: CameraPosition

    init {
        viewModelScope.launch {
            loadCity()
        }
    }

    fun getCameraPosition(point: Point, zoom: ZoomDirection = ZoomDirection.Nothing): CameraPosition {
        when (zoom) {
            is ZoomDirection.Plus -> currentZoom += 1
            is ZoomDirection.Minus -> currentZoom -= 1
            is ZoomDirection.Nothing -> {}
        }
        currentCameraPosition = CameraPosition.fromLatLngZoom(
            getLatLngCoords(point.coords),
            currentZoom
        )
        return currentCameraPosition
    }

    fun getNewCameraPosition(zoom: ZoomDirection = ZoomDirection.Nothing): CameraPosition {
        return getCameraPosition(currentPoint, zoom)
    }

    fun getLatLngCoords(coords: String): LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }

    private fun loadCity() {
        viewModelScope.launch {
            _screenState.emit(TakeoutMapScreenState.Loading)
            getCurrentCityUseCase
                .getCurrentSettingsFlow()
                .collect {
                    if (it?.city == null) return@collect
                    val city = it.city
                    currentPoint = city.points[0]
                    _screenState.emit(TakeoutMapScreenState.Content(city, currentPoint))
                }
        }
    }

    fun pointChosed() {
        viewModelScope.launch {
            setPointSettingsUseCase.setPoint(currentPoint)
        }
    }

    fun changeScreenState(state: TakeoutMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }

    fun changePoint(point: Point) {
        currentPoint = point
    }

}