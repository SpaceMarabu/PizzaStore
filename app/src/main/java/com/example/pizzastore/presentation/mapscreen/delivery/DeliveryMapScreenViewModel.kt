package com.example.pizzastore.presentation.mapscreen.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.AddressDetails
import com.example.pizzastore.domain.entity.AddressPart
import com.example.pizzastore.domain.entity.AddressState
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
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _screenEvents = MutableSharedFlow<ScreenEvent>()
    val screenEvents
        get() = _screenEvents.asSharedFlow()

    private val addressChangingFlow = MutableSharedFlow<AddressPart>()
    private val tempAddressStateFlow = MutableStateFlow(AddressDetails())

    private lateinit var currentCameraPosition: CameraPosition

    private val _addressByGeocodeFlow = MutableStateFlow(AddressState())
    val addressByGeocodeFlow
        get() = _addressByGeocodeFlow.asStateFlow()

    private var lastPosition = "0, 0"

    private val _currentPositionHandleFlow = MutableStateFlow<String?>(null)

    init {
        changeScreenState(DeliveryMapScreenState.Content)
        startEmitting()
        changingTempAddressFlow()
    }

    //<editor-fold desc="saveClick">
    fun saveClick() {
        viewModelScope.launch {
            _screenEvents.emit(ScreenEvent.SaveClicked)
            delay(100)
            val deliverType = DeliveryType.DELIVERY_TO
            val address = tempAddressStateFlow.value
            val geoPoint = lastPosition
            val details = DeliveryDetails(
                type = deliverType,
                deliveryAddress = address,
                pizzaStore = null,
                deliveryGeoPoint = geoPoint
            )
            if (!address.address.isNullOrEmpty()) {
                sendDeliveryDetailsUseCase.sendDetails(details)
                _screenEvents.emit(ScreenEvent.ExitScreen)
            }
        }
    }
//</editor-fold>

    //<editor-fold desc="sendAddressPart">
    fun sendAddressPart(part: AddressPart) {
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
                val currentAddressResult = when (val changeKind = it) {
                    is AddressPart.AddressLine -> currentAddressVal.copy(address = changeKind.address)
                    is AddressPart.Comment -> currentAddressVal.copy(comment = changeKind.comment)
                    is AddressPart.DoorCode -> currentAddressVal.copy(doorCode = changeKind.doorCode)
                    is AddressPart.Entrance -> currentAddressVal.copy(entrance = changeKind.entrance)
                    is AddressPart.Floor -> currentAddressVal.copy(floor = changeKind.floor)
                    is AddressPart.Apartment -> currentAddressVal.copy(apartment = changeKind.apartment)
                }
                tempAddressStateFlow.emit(currentAddressResult)
            }
        }
    }
//</editor-fold>

    //<editor-fold desc="inputTextStarted">
    fun inputTextStarted() {
        viewModelScope.launch {
            val currentState = _addressByGeocodeFlow.value
            _addressByGeocodeFlow.emit(currentState.copy(isInputTextStarted = true))
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
            _addressByGeocodeFlow.emit(addressState)
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