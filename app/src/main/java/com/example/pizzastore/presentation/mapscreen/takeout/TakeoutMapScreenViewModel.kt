package com.example.pizzastore.presentation.mapscreen.takeout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.usecases.GetCurrentCityUseCase
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TakeoutMapScreenViewModel @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow<TakeoutMapScreenState>(TakeoutMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()


    private lateinit var currentPoint: Point
    private var currentZoom = 14f
    private lateinit var currentCameraPosition: CameraPosition


//    private val _currentPoint = MutableSharedFlow<Point>(
//        replay = 1,
//        extraBufferCapacity = 1,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
//    )
//    val currentPoint
//        get() = _currentPoint.asSharedFlow()

    init {
        viewModelScope.launch {
            loadCity()
        }
    }

    fun getStartPoint(): Point {
        val currentScreenState = screenState.value as TakeoutMapScreenState.Content
        return currentScreenState.city.points[0]
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

    private suspend fun loadCity() {
        _screenState.emit(TakeoutMapScreenState.Loading)
        getCurrentCityUseCase
            .getCurrentCityFlow()
            .collect {
                if (it == null) return@collect
                currentPoint = it.points[0]
                _screenState.emit(TakeoutMapScreenState.Content(it, currentPoint))
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