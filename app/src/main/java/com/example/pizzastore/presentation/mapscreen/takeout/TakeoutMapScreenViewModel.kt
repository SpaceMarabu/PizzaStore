package com.example.pizzastore.presentation.mapscreen.takeout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.usecases.GetCurrentSettingsUseCase
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

    //<editor-fold desc="getCameraPosition">
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
    //</editor-fold>

    //<editor-fold desc="getNewCameraPosition">
    fun getNewCameraPosition(zoom: ZoomDirection = ZoomDirection.Nothing) =
        getCameraPosition(currentPoint, zoom)
    //</editor-fold>

    //<editor-fold desc="getLatLngCoords">
    fun getLatLngCoords(coords: String): LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }
    //</editor-fold>

    //<editor-fold desc="loadCity">
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
    //</editor-fold>

    //<editor-fold desc="pointChoose">
    fun pointChoose() {
        viewModelScope.launch {
            setPointSettingsUseCase.setPoint(currentPoint)
        }
    }
    //</editor-fold>

    //<editor-fold desc="changeScreenState">
    fun changeScreenState(state: TakeoutMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }
    //</editor-fold>

    //<editor-fold desc="changePoint">
    fun changePoint(point: Point) {
        currentPoint = point
    }
    //</editor-fold>

}