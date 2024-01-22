package com.example.pizzastore.presentation.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(points: List<String>) {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var permissionGranted by remember {
        mutableStateOf(false)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { it ->
            permissionGranted = !it.values.contains(false)
        }

    SideEffect {
        if (!permissionGranted) {
            // ask for permission
            permissionLauncher.launch(permissions)
        }
    }

    val pointsToLatLng: MutableList<LatLng> = mutableListOf()
    points.forEach {
        val point = it.split(",").toTypedArray()
        pointsToLatLng.add(
            LatLng(
                point[0].toDouble(),
                point[1].toDouble()
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pointsToLatLng[0], 17f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to the MapsApp!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (permissionGranted) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.weight(1f),
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(compassEnabled = true)
            ) {
                GoogleMarkers()
                Polyline(
                    points = listOf(
                        LatLng(44.811058, 20.4617586),
                        LatLng(44.811058, 20.4627586),
                        LatLng(44.810058, 20.4627586),
                        LatLng(44.809058, 20.4627586),
                        LatLng(44.809058, 20.4617586)
                    )
                )
            }
        }
    }
}


@Composable
fun GoogleMarkers() {
    Marker(
        state = rememberMarkerState(position = LatLng(44.811058, 20.4617586)),
        title = "Marker1",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    )
    Marker(
        state = rememberMarkerState(position = LatLng(44.811058, 20.4627586)),
        title = "Marker2",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
    )
    Marker(
        state = rememberMarkerState(position = LatLng(44.810058, 20.4627586)),
        title = "Marker3",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
    )
    Marker(
        state = rememberMarkerState(position = LatLng(44.809058, 20.4627586)),
        title = "Marker4",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    )
    Marker(
        state = rememberMarkerState(position = LatLng(44.809058, 20.4617586)),
        title = "Marker5",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
    )
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