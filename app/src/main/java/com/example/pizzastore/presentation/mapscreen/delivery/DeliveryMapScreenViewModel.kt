package com.example.pizzastore.presentation.mapscreen.delivery

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

class DeliveryMapScreenViewModel @Inject constructor(
) : ViewModel() {

    private val _screenState =
        MutableStateFlow<DeliveryMapScreenState>(DeliveryMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()

    private lateinit var currentCameraPosition: CameraPosition



    init {
        changeScreenState(DeliveryMapScreenState.Content)
    }
    fun getCameraPosition(
        coords: String,
        zoom: Float = 16f
    ): CameraPosition {
        currentCameraPosition = CameraPosition.fromLatLngZoom(
            getLatLngCoords(coords),
            zoom
        )
        return currentCameraPosition
    }

//    fun getNewCameraPosition(zoom: ZoomDirection = ZoomDirection.Nothing): CameraPosition {
//        return getCameraPosition(currentPoint, zoom)
//    }

    fun getLatLngCoords(coords: String): LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }


    fun changeScreenState(state: DeliveryMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }

}