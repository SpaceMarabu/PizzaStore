package com.example.pizzastore.presentation.mapscreen.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.usecases.GetAddressUseCase
import com.example.pizzastore.domain.usecases.GetPathUseCase
import com.example.pizzastore.presentation.mapscreen.MapConsts
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeliveryMapScreenViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val getPathUseCase: GetPathUseCase
) : ViewModel() {

    private val _screenState =
        MutableStateFlow<DeliveryMapScreenState>(DeliveryMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()

    private lateinit var currentCameraPosition: CameraPosition

    private val _addressFlow =
        MutableStateFlow<Address?>(null)
    val addressFlow
        get() = _addressFlow.asStateFlow()


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

    fun postLatlang(latlng: String) {
        viewModelScope.launch {
            val address = getAddressUseCase.getAddress(latlng)
            val path = getPathUseCase.getPath(MapConsts.PIZZA_STORE_LOCATION, latlng)
            _addressFlow.emit(
                address.copy(
                    path = if (path != Path.EMPTY_PATH) path else null
                )
            )
        }
    }


    fun changeScreenState(state: DeliveryMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }

}