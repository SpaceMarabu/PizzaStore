package com.example.pizzastore.presentation.mapscreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.funs.getBitmapDescriptorFromVector
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(paddingValues: PaddingValues) {

    val component = getApplicationComponent()
    val viewModel: MapScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState()

    when (screenState.value) {
        is MapScreenState.Initial -> {}
        is MapScreenState.Loading -> {
            CircularLoading()
        }
        is MapScreenState.Content -> {
            val currentScreenState = screenState.value as MapScreenState.Content
            MapScreenContent(
                paddingValues = paddingValues,
                points = currentScreenState.city.points
            )
        }
    }
}


@Composable
fun MapScreenContent(paddingValues: PaddingValues, points: List<Point>) {

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


    val pointsToLatLng: MutableList<LatLng> = mutableListOf()
    points.forEach {
        val point = it.coords
        pointsToLatLng.add(
            LatLng(
                point[0].toDouble(),
                point[1].toDouble()
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pointsToLatLng[0], 14f)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
            .background(MaterialTheme.colors.background)
    ) {
        if (permissionGranted) {
            Box {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            LocalContext.current,
                            R.raw.raw_style
                        )
                    ),
                    uiSettings = MapUiSettings(
                        compassEnabled = true,
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = true,
                        indoorLevelPickerEnabled = true,

                        )
                ) {
                    pointsToLatLng.forEach {
                        Marker(
                            state = rememberMarkerState(position = it),
                            icon = getBitmapDescriptorFromVector(
                                LocalContext.current, R.drawable.ic_contacts_orange
                            )
                        )
                    }
                }
//                Column {
//                    Spacer(modifier = Modifier.weight(1f))
//                    Divider(
//                        modifier = Modifier
//                            .padding(start = 180.dp, end = 180.dp)
//                            .clip(RoundedCornerShape(10.dp)),
//                        color = Color.LightGray,
//                        thickness = 4.dp
//                    )
//                    Row {
//                        Spacer(modifier = Modifier.weight(1f))
//                        Icon(
//                            modifier = Modifier
//                                .padding(end = 8.dp)
//                                .size(30.dp),
//                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_cross),
//                            contentDescription = null
//                        )
//                    }
//                    Text("Пиццуха")
//                }
            }

        }
    }
}


@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

//    val positionSaver = Saver<CameraPosition, MapPosition>(
//        save = {
//            val item = it
//            val target = it.target
//            MapPosition(
//                MapPoint(
//                    target.latitude,
//                    target.longitude
//                ),
//                zoom = it.zoom,
//                azimuth = it.azimuth,
//                tilt = it.tilt
//            )
//        },
//        restore = {
//            val position = it
//            CameraPosition(
//                Point(it.target.x, it.target.y),
//                it.zoom,
//                it.azimuth,
//                it.tilt
//            )
//        }
//    )

//    ComposableLifecycle { lifecycleOwner, event ->
//        when (event) {
//            Lifecycle.Event.ON_START -> {
//                MapKitFactory.getInstance().onStart()
//            }
//
//            Lifecycle.Event.ON_STOP -> {
//                MapKitFactory.getInstance().onStop()
//            }
//
//            else -> {}
//        }
//    }