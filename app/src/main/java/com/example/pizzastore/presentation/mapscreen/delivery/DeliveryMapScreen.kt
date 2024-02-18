package com.example.pizzastore.presentation.mapscreen.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.funs.pxToDp
import com.example.pizzastore.presentation.mapscreen.ChangeMapPosition
import com.example.pizzastore.presentation.mapscreen.MapConsts.BASE_LOCATION
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
fun DeliveryMapScreen(paddingValues: PaddingValues) {

    val component = getApplicationComponent()
    val viewModel: DeliveryMapScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState()

    when (screenState.value) {
        is DeliveryMapScreenState.Initial -> {}
        is DeliveryMapScreenState.Loading -> {
            CircularLoading()
        }

        is DeliveryMapScreenState.Content -> {
            DeliveryMapScreenContent(
                paddingValues = paddingValues,
                viewModel
            )
        }
    }
}

@Composable
fun DeliveryMapScreenContent(
    paddingValues: PaddingValues,
    viewModel: DeliveryMapScreenViewModel
) {

    var permissionGranted by remember {
        mutableStateOf(false)
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
            permissionGranted = !it.values.contains(false)
        }

    SideEffect {
        if (!permissionGranted) {
            permissionLauncher.launch(permissions)
        }
    }

    Log.d("TEST_TEST", "outer rec")

    val cameraPositionState = rememberCameraPositionState {
        position = viewModel.getCameraPosition("0, 0", 1f)
    }


    val currentLocation = remember {
        mutableStateOf(BASE_LOCATION)
    }

    Log.d("TEST_LOC", currentLocation.value)


    val scope = rememberCoroutineScope()
    val currentAddress = viewModel.addressFlow.collectAsState()
    Log.d("TEST_API", currentAddress.value.toString())

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
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .background(MaterialTheme.colors.background)
            ) {
                if (permissionGranted) {
                    Location() {
                        val latLngString = "${it.latitude}, ${it.longitude}"
                        currentLocation.value = latLngString
                        viewModel.postLatlang(latLngString)
                    }
                    MapWithPin(cameraPositionState = cameraPositionState)
                    EnterFrom() {
                        TODO()
                    }
                    LocationButton(
                        currentLocation = currentLocation.value,
                        baseLocation = BASE_LOCATION
                    ) {
                        isLocationClicked = true
                    }
                }
            }
        }
    }
}

@Composable
fun MapWithPin(cameraPositionState: CameraPositionState) {
    Column {
        val displayMetrics: DisplayMetrics =
            LocalContext.current.resources.displayMetrics
        val displayWithoutBottomContent =
            displayMetrics.heightPixels.toFloat().pxToDp() - 350.dp

        Box(
            modifier = Modifier.weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier
                    .height(displayWithoutBottomContent),
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
                modifier = Modifier.height(displayWithoutBottomContent),
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

@Composable
fun EnterFrom(onSaveClicked: () -> Unit) {

    LazyColumn(
        modifier = Modifier
            .height(350.dp)
            .background(Color.White)
    ) {
        item {
            TextFieldDelivery(
                label = "Город, улица и дом",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
        }
        item {
            RowWithTwoTextField(
                label1 = "Подъезд",
                label2 = "Код на двери"
            )
        }
        item {
            RowWithTwoTextField(
                label1 = "Этаж",
                label2 = "Квартира"
            )
        }
        item {
            TextFieldDelivery(
                label = "Комментарий к адресу",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp
                    )
            )
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
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .clickable {
                        onSaveClicked()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сохранить",
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun LocationButton(
    currentLocation: String,
    baseLocation: String,
    onLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
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


@Composable
fun Location(onChange: (LatandLong) -> Unit) {

    var locationCallBack: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null

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
        startLocationUpdates(locationCallBack, fusedLocationClient, context = context)
    }
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    locationCallBack: LocationCallback?,
    fusedLocationClient: FusedLocationProviderClient?,
    timeInterval: Long = 1000,
    context: Context
) {
    locationCallBack?.let {
        val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, timeInterval).apply {
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

@Composable
fun TextFieldDelivery(label: String, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier,
        label = { Text(text = label) },
        value = text,
        onValueChange = { text = it },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.LightGray,
            unfocusedLabelColor = Color.LightGray,
            backgroundColor = Color.White,
            textColor = Color.Black,
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            cursorColor = Color.Gray
        ),
        shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
    )
}

@Composable
fun RowWithTwoTextField(label1: String, label2: String) {
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
                .padding(end = paddingBetweenTF)
        )
        TextFieldDelivery(
            label = label2,
            modifier = Modifier
                .width(textFieldWidth)
                .padding(start = paddingBetweenTF)
        )
    }
}