package com.example.pizzastore.presentation.mapscreen.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.DisplayMetrics
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.AddressLineInputResult
import com.example.pizzastore.domain.entity.AddressPart
import com.example.pizzastore.presentation.utils.CircularLoading
import com.example.pizzastore.presentation.utils.pxToDp
import com.example.pizzastore.presentation.mapscreen.ChangeMapPosition
import com.example.pizzastore.presentation.mapscreen.MapConsts.BASE_LOCATION
import com.example.pizzastore.presentation.mapscreen.RequestPermissionsButton
import com.example.pizzastore.presentation.utils.getOutlinedColors
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DeliveryMapScreen(
    exitScreen: () -> Unit
) {

    val component = getApplicationComponent()
    val viewModel: DeliveryMapScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.screenEvents.collect {
            when (it) {
                ScreenEvent.ExitScreen -> {
                    exitScreen()
                }
                else -> {}
            }
        }
    }

    when (screenState.value) {
        is DeliveryMapScreenState.Initial -> {}
        is DeliveryMapScreenState.Loading -> {
            CircularLoading()
        }

        is DeliveryMapScreenState.Content -> {
            DeliveryMapScreenContent(viewModel)
        }
    }
}

//<editor-fold desc="DeliveryMapScreenContent">
@Composable
fun DeliveryMapScreenContent(
    viewModel: DeliveryMapScreenViewModel
) {

    var permissionGranted by remember {
        mutableStateOf(false)
    }
    var needRequestPermission by remember {
        mutableStateOf(false)
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            permissionGranted = permissionsMap.values.all { it }
                    && permissionsMap.values.isNotEmpty()
        }

    if (needRequestPermission or !permissionGranted) {
        SideEffect {
            permissionLauncher.launch(permissions)
        }
        needRequestPermission = false
    }

    val cameraPositionState = rememberCameraPositionState {
        position = viewModel.getCameraPosition("0, 0", 1f)
    }

    val currentLocation = remember {
        mutableStateOf(BASE_LOCATION)
    }

    val scope = rememberCoroutineScope()

    var mapWasMoved = remember {
        false
    }
    if (cameraPositionState.isMoving) {
        mapWasMoved = true
    }

    var isLocationClicked by remember {
        mutableStateOf(true)
    }
    if (isLocationClicked) {
        val newCameraLocation = currentLocation.value
        ChangeMapPosition(
            cameraPositionState = cameraPositionState,
            newCameraPosition = viewModel.getCameraPosition(newCameraLocation),
            scope = scope
        )
        isLocationClicked = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (permissionGranted) {
            Location() {
                if (!mapWasMoved) {
                    val latLngString = "${it.latitude},${it.longitude}"
                    currentLocation.value = latLngString
                    isLocationClicked = true
                }
            }
            if (!cameraPositionState.isMoving) {
                viewModel.postLatLang(cameraPositionState.position.target)
            }

            val displayMetrics: DisplayMetrics =
                LocalContext.current.resources.displayMetrics
            val displayWithoutBottomContent =
                displayMetrics.heightPixels.toFloat().pxToDp() - 350.dp

            Box(
                modifier = Modifier
                    .height(displayWithoutBottomContent)
            ) {
                MapWithPin(
                    cameraPositionState = cameraPositionState,
                    height = displayWithoutBottomContent
                )
                LocationButton(
                    currentLocation = currentLocation.value,
                    baseLocation = BASE_LOCATION
                ) {
                    isLocationClicked = true
                }
            }
            EnterForm(viewModel)
        } else {
            RequestPermissionsButton {
                needRequestPermission = true
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="MapWithPin">
@Composable
fun MapWithPin(
    cameraPositionState: CameraPositionState,
    height: Dp
) {
    Column {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier
                    .height(height),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                        LocalContext.current,
                        R.raw.raw_style
                    )
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    zoomControlsEnabled = false,
                    indoorLevelPickerEnabled = true,
                )
            ) {
            }
            Column(
                modifier = Modifier
                    .height(height),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(60.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_pin),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="EnterForm">
@Composable
fun EnterForm(
    viewModel: DeliveryMapScreenViewModel
) {

    var errorState by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .height(350.dp)
            .background(Color.White)
    ) {
        item {
            TextFieldAddress(
                label = stringResource(R.string.city_street_house_lable),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                viewModel = viewModel
            ) {
                errorState = it.isError
                viewModel.sendAddressPart(AddressPart.AddressLine(it.line))
            }
        }
        item {
            RowWithTwoTextField(
                label1 = stringResource(R.string.gate_label),
                label2 = stringResource(R.string.door_code_label),
                viewModel = viewModel,
                onSaveClicked1 = {
                    viewModel.sendAddressPart(AddressPart.Entrance(it))
                }
            ) {
                viewModel.sendAddressPart(AddressPart.DoorCode(it))
            }
        }
        item {
            RowWithTwoTextField(
                label1 = stringResource(R.string.floor_label),
                label2 = stringResource(R.string.room_number_label),
                viewModel = viewModel,
                onSaveClicked1 = {
                    viewModel.sendAddressPart(AddressPart.Floor(it))
                }
            ) {
                viewModel.sendAddressPart(AddressPart.Apartment(it))
            }
        }
        item {
            TextFieldDelivery(
                label = stringResource(R.string.address_comment_label),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp
                    ),
                viewModel = viewModel
            ) {
                viewModel.sendAddressPart(AddressPart.Comment(it))
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .background(colorResource(id = R.color.orange))
                    .clickable {
                        viewModel.saveClick()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.save_button_text),
                    color = Color.Black
                )
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="LocationButton">
@Composable
fun LocationButton(
    currentLocation: String,
    baseLocation: String,
    onLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        val isLocationNotBase = currentLocation != baseLocation

        Row {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        if (isLocationNotBase)
                            Color.White else Color.Gray.copy(alpha = 0.2f)
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            enabled = isLocationNotBase
                        ) {
                            onLocationClick()
                        },
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_compass_row),
                    contentDescription = null,
                    tint = if (isLocationNotBase)
                        Color.Black else Color.Gray.copy(alpha = 0.2f)
                )
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="Location">
@Composable
fun Location(onChange: (LatandLong) -> Unit) {

    val locationCallBack: LocationCallback?
    val fusedLocationClient: FusedLocationProviderClient?

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (lo in p0.locations) {
                onChange(LatandLong(lo.latitude, lo.longitude))
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        startLocationUpdates(
            locationCallBack,
            fusedLocationClient,
            context = context
        )
    }
}
//</editor-fold>

//<editor-fold desc="startLocationUpdates">
@SuppressLint("MissingPermission")
fun startLocationUpdates(
    locationCallBack: LocationCallback?,
    fusedLocationClient: FusedLocationProviderClient?,
    timeInterval: Long = 1000,
    context: Context
) {
    locationCallBack?.let {
        val locationRequest =
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, timeInterval).apply {
                setMinUpdateDistanceMeters(10.0f)
                setGranularity(GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            it,
            Looper.getMainLooper()
        )
    }
}
//</editor-fold>

//<editor-fold desc="TextFieldDelivery">
@Composable
fun TextFieldDelivery(
    label: String,
    modifier: Modifier = Modifier,
    viewModel: DeliveryMapScreenViewModel,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onSaveClicked: (String) -> Unit
) {

    var text by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.screenEvents.collect {
            when (it) {
                ScreenEvent.SaveClicked -> {
                    onSaveClicked(text)
                }
                else -> {}
            }
        }
    }

    OutlinedTextField(
        modifier = modifier,
        label = { Text(text = label) },
        value = text,
        onValueChange = {
            text = it
        },
        keyboardOptions = keyboardOptions,
        shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
        colors = getOutlinedColors()
    )
}
//</editor-fold>

//<editor-fold desc="TextFieldAddress">
@Composable
fun TextFieldAddress(
    label: String,
    modifier: Modifier = Modifier,
    viewModel: DeliveryMapScreenViewModel,
    onSaveClicked: (AddressLineInputResult) -> Unit
) {

    var text by remember { mutableStateOf("") }

    var isError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.screenEvents.collect {
            when (it) {
                ScreenEvent.SaveClicked -> {
                    if (text.isBlank()) {
                        isError = true
                    }
                    val addressLineInputResult = AddressLineInputResult(text, isError)
                    onSaveClicked(addressLineInputResult)
                }
                else -> {}
            }
        }
    }

    val currentAddress by viewModel.addressByGeocodeFlow.collectAsState()
    if (!currentAddress.isInputTextStarted) {
        val currentAddressValue = currentAddress
        if (currentAddressValue.address?.street != null) {
            text = ("${currentAddressValue.address.street}"
                    + if (
                currentAddressValue.address.houseNumber != null
            ) {
                ", ${currentAddressValue.address.houseNumber}"
            } else {
                ""
            })
        }
    }

    OutlinedTextField(
        modifier = modifier,
        label = { Text(text = label) },
        value = text,
        onValueChange = {
            viewModel.inputTextStarted()
            isError = false
            text = it
        },
        isError = isError,
        colors = getOutlinedColors(),
        shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
    )
}
//</editor-fold>

//<editor-fold desc="RowWithTwoTextField">
@Composable
fun RowWithTwoTextField(
    label1: String,
    label2: String,
    viewModel: DeliveryMapScreenViewModel,
    onSaveClicked1: (String) -> Unit,
    onSaveClicked2: (String) -> Unit
) {
    val startRowDpPadding = 16.dp
    val endRowDpPadding = 16.dp
    val paddingBetweenTF = 4.dp
    val displayMetrics: DisplayMetrics = LocalContext.current.resources.displayMetrics
    val halfDisplayDp = displayMetrics.widthPixels / 2f
    val textFieldWidth = halfDisplayDp.pxToDp() - startRowDpPadding - (paddingBetweenTF / 2)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                start = startRowDpPadding,
                end = endRowDpPadding
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        TextFieldDelivery(
            label = label1,
            modifier = Modifier
                .width(textFieldWidth)
                .padding(end = paddingBetweenTF),
            viewModel = viewModel,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        ) {
            onSaveClicked1(it)
        }
        TextFieldDelivery(
            label = label2,
            modifier = Modifier
                .width(textFieldWidth)
                .padding(start = paddingBetweenTF),
            viewModel = viewModel,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        ) {
            onSaveClicked2(it)
        }
    }
}
//</editor-fold>
