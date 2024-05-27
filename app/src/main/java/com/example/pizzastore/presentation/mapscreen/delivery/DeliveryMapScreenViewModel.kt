package com.example.pizzastore.presentation.mapscreen.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.AddressDetails
import com.example.pizzastore.domain.entity.AddressParts
import com.example.pizzastore.domain.entity.AddressState
import com.example.pizzastore.domain.entity.AddressWithPath
import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.usecases.GetAddressByGeoCodeUseCase
import com.example.pizzastore.domain.usecases.GetPathUseCase
import com.example.pizzastore.domain.usecases.SendDeliveryDetailsUseCase
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
    private val getPathUseCase: GetPathUseCase,
    private val sendDeliveryDetailsUseCase: SendDeliveryDetailsUseCase
) : ViewModel() {

    private val _screenState =
        MutableStateFlow<DeliveryMapScreenState>(DeliveryMapScreenState.Initial)
    val screenState
        get() = _screenState.asStateFlow()

    private val _saveClickedFlow =
        MutableStateFlow(false)
    val saveClickedFlow
        get() = _saveClickedFlow.asStateFlow()

    private val addressChangingFlow = MutableSharedFlow<AddressParts>()
    private val tempAddressStateFlow = MutableStateFlow(AddressDetails())

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

    fun onLeavingScreen() {
        val deliverType = DeliveryType.DELIVERY_TO
        val address = tempAddressStateFlow.value
        val geoPoint = lastPosition
        val details = DeliveryDetails(
            type = deliverType,
            deliveryAddress = address,
            pizzaStore = null,
            deliveryGeoPoint = geoPoint
        )
        sendDeliveryDetailsUseCase.sendDetails(details)
    }

    //<editor-fold desc="saveClick">
    fun saveClick() {
        viewModelScope.launch {
            _saveClickedFlow.emit(true)
            delay(1000)
            _saveClickedFlow.emit(false)
        }
    }
    //</editor-fold>

    //<editor-fold desc="sendAddressPart">
    fun sendAddressPart(part: AddressParts) {
        viewModelScope.launch {
            addressChangingFlow.emit(part)
        }
    }
    //</editor-fold>

    //<editor-fold desc="changingTempAddressFlow">
    private fun changingTempAddressFlow() {
        viewModelScope.launch {
            addressChangingFlow.collect {
                val currentAddressVal = tempAddressStateFlow.value
                val currentAddressResult = when (it) {
                    is AddressParts.AddressLine -> currentAddressVal.copy(address = it.address)
                    is AddressParts.Comment -> currentAddressVal.copy(comment = it.comment)
                    is AddressParts.DoorCode -> currentAddressVal.copy(doorCode = it.doorCode)
                    is AddressParts.Entrance -> currentAddressVal.copy(entrance = it.entrance)
                    is AddressParts.Floor -> currentAddressVal.copy(floor = it.floor)
                    is AddressParts.Apartment -> currentAddressVal.copy(apartment = it.apartment)
                }
                tempAddressStateFlow.emit(currentAddressResult)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="inputTextStarted">
    fun inputTextStarted() {
        viewModelScope.launch {
            val currentState = _addressFlow.value
            _addressFlow.emit(currentState.copy(isInputTextStarted = true))
        }
    }
    //</editor-fold>

    //<editor-fold desc="startEmitting">
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
    //</editor-fold>

    //<editor-fold desc="postLatLang">
    fun postLatLang(latlng: LatLng) {
        viewModelScope.launch {
            val currentPosition = "${latlng.latitude},${latlng.longitude}"
            _currentPositionHandleFlow.emit(currentPosition)
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCameraPosition">
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
    //</editor-fold>

    //<editor-fold desc="getLatLngCoords">
    private fun getLatLngCoords(coords: String): LatLng {
        val splitedCoords = coords.split(",")
        return LatLng(
            splitedCoords[0].toDouble(),
            splitedCoords[1].toDouble()
        )
    }
    //</editor-fold>

    //<editor-fold desc="requestAddress">
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
    //</editor-fold>

    //<editor-fold desc="changeScreenState">
    private fun changeScreenState(state: DeliveryMapScreenState) {
        viewModelScope.launch {
            _screenState.emit(state)
        }
    }
    //</editor-fold>

}