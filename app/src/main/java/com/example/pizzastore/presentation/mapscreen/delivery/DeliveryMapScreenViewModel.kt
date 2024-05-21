package com.example.pizzastore.presentation.mapscreen.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.AddressResult
import com.example.pizzastore.domain.entity.AddressState
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.usecases.GetAddressByGeoCodeUseCase
import com.example.pizzastore.domain.usecases.GetPathUseCase
import com.example.pizzastore.presentation.mapscreen.MapConsts
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeliveryMapScreenViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressByGeoCodeUseCase,
    private val getPathUseCase: GetPathUseCase
) : ViewModel() {

    private val _screenState =
        MutableStateFlow<DeliveryMapScreenState>(DeliveryMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()

    private val _saveClickedFlow =
        MutableStateFlow(false)
    val saveClickedFlow
        get() = _saveClickedFlow.asStateFlow()

    private val addressChangingFlow = MutableSharedFlow<AddressResult>()
    private val tempAddressStateFlow = MutableStateFlow(AddressResult.DeliveryInfo())

    private lateinit var currentCameraPosition: CameraPosition

    private val _addressFlow = MutableStateFlow(AddressState())
    val addressFlow
        get() = _addressFlow.asStateFlow()

    private var lastPosition = "0, 0"


    private val _currentPositionHandleFlow = MutableStateFlow<String?>(null)

    init {
        changeScreenState(DeliveryMapScreenState.Content)
        startEmitting()
        changingTempAddressFlow()
    }

    fun saveClick() {
        viewModelScope.launch {
            _saveClickedFlow.emit(true)
            delay(1000)
            _saveClickedFlow.emit(false)
        }
    }

    fun sendAddressPart(part: AddressResult) {
        viewModelScope.launch {
            addressChangingFlow.emit(part)
        }
    }

    private fun changingTempAddressFlow() {
        viewModelScope.launch {
            addressChangingFlow.collect {
                val currentAddressVal = tempAddressStateFlow.value
                val currentAddressResult = when (it) {
                    is AddressResult.DeliveryInfo -> {currentAddressVal}
                    is AddressResult.AddressLine -> currentAddressVal.copy(address = it.address)
                    is AddressResult.Comment -> currentAddressVal.copy(comment = it.comment)
                    is AddressResult.DoorCode -> currentAddressVal.copy(doorCode = it.doorCode)
                    is AddressResult.Entrance -> currentAddressVal.copy(entrance = it.entrance)
                    is AddressResult.Floor -> currentAddressVal.copy(floor = it.floor)
                    is AddressResult.Apartment -> currentAddressVal.copy(apartment = it.apartment)
                }
                tempAddressStateFlow.emit(currentAddressResult)
                Log.d("TEST_ADDRESS", tempAddressStateFlow.value.toString())
            }
        }
    }


    //<editor-fold desc="inputTextStarted">
    fun inputTextStarted() {
        viewModelScope.launch {
            val currentState = _addressFlow.value
            _addressFlow.emit(currentState.copy(isInputTextStarted = true))
        }
    }
    //</editor-fold>

    private fun startEmitting() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentPosition = _currentPositionHandleFlow.value
                if (currentPosition != null && currentPosition != lastPosition) {
                    Log.d("TEST_API", "request")
                    requestAddress(currentPosition)
                    lastPosition = currentPosition
                }
            }
        }
    }

    fun postLatLang(latlng: LatLng) {
        viewModelScope.launch {
            val currentPosition = "${latlng.latitude},${latlng.longitude}"
            _currentPositionHandleFlow.emit(currentPosition)
        }
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

    fun getLatLngCoords(coords: String): LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }

    private fun requestAddress(latlng: String) {
        viewModelScope.launch {
            val address = getAddressUseCase.getAddress(latlng)
            val path = getPathUseCase.getPath(MapConsts.PIZZA_STORE_LOCATION, latlng)
            val addressState = AddressState(
                address = address.copy(
                    path = if (path != Path.EMPTY_PATH) path else null
                ),
            )
            _addressFlow.emit(addressState)
        }
    }


    fun changeScreenState(state: DeliveryMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }

}