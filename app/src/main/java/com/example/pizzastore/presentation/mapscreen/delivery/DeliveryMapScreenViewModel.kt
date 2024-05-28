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

    private val addressChangingFlow = MutableSharedFlow<AddressPart>()
    private val tempAddressStateFlow = MutableStateFlow(AddressDetails())

    private val addressPartHasCollectedMap =
        MutableStateFlow<MutableMap<AddressPart, Boolean>>(mutableMapOf())

    private lateinit var currentCameraPosition: CameraPosition

    private val _addressFlow = MutableStateFlow(AddressState())
    val addressFlow
        get() = _addressFlow.asStateFlow()

    private var lastPosition = "0, 0"

    private val _currentPositionHandleFlow = MutableStateFlow<String?>(null)

    init {
        changeScreenState(DeliveryMapScreenState.Content)
        initEmptyMapForCollectingAddressParts()
        startEmitting()
        changingTempAddressFlow()
    }

    //<editor-fold desc="onLeavingScreen">
    fun onLeavingScreen() {
        viewModelScope.launch {
            addressPartHasCollectedMap.collect { addressPartMap ->
                if (addressPartMap.values.all { it }) {
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
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="saveClick">
    fun saveClick() {
        initEmptyMapForCollectingAddressParts()
        viewModelScope.launch {
            _saveClickedFlow.emit(true)
            addressPartHasCollectedMap.collect { addressPartMap ->
                if (addressPartMap.values.all { it }) {
                    _saveClickedFlow.emit(false)
                }
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
                val changeKind = it
                val currentAddressResult = when (changeKind) {
                    is AddressPart.AddressLine -> currentAddressVal.copy(address = changeKind.address)
                    is AddressPart.Comment -> currentAddressVal.copy(comment = changeKind.comment)
                    is AddressPart.DoorCode -> currentAddressVal.copy(doorCode = changeKind.doorCode)
                    is AddressPart.Entrance -> currentAddressVal.copy(entrance = changeKind.entrance)
                    is AddressPart.Floor -> currentAddressVal.copy(floor = changeKind.floor)
                    is AddressPart.Apartment -> currentAddressVal.copy(apartment = changeKind.apartment)
                }
                addressPartCollected(changeKind)
                tempAddressStateFlow.emit(currentAddressResult)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="addressPartCollected">
    private suspend fun addressPartCollected(part: AddressPart) {
        //создаю объект с новым хэшем для stateFlow
        val addressPartFlowValue = (
                mapOf<AddressPart, Boolean>() + addressPartHasCollectedMap.value
                ).toMutableMap()
        val key = when (part) {
            is AddressPart.AddressLine -> AddressPart.AddressLine()
            is AddressPart.Apartment -> AddressPart.Apartment()
            is AddressPart.Comment -> AddressPart.Comment()
            is AddressPart.DoorCode -> AddressPart.DoorCode()
            is AddressPart.Entrance -> AddressPart.Entrance()
            is AddressPart.Floor -> AddressPart.Floor()
        }
        addressPartFlowValue[key] = true
        addressPartHasCollectedMap.emit(addressPartFlowValue)
    }
    //</editor-fold>

    //<editor-fold desc="initEmptyMapForCollectingAddressParts">
    private fun initEmptyMapForCollectingAddressParts() {
        val resultMap = hashMapOf<AddressPart, Boolean>()
        resultMap[AddressPart.AddressLine()] = false
        resultMap[AddressPart.Comment()] = false
        resultMap[AddressPart.DoorCode()] = false
        resultMap[AddressPart.Entrance()] = false
        resultMap[AddressPart.Floor()] = false
        resultMap[AddressPart.Apartment()] = false
        addressPartHasCollectedMap.value = resultMap
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