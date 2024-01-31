package com.example.pizzastore.presentation.mapscreen

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.funs.getBitmapDescriptorFromVector
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch


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
                currentScreenState.city,
                viewModel
            )
        }
    }
}


@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    city: City,
    viewModel: MapScreenViewModel
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
        position = CameraPosition.fromLatLngZoom(
            viewModel.getStartCoords(),
            14f
        )
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
                        zoomControlsEnabled = false,
                        indoorLevelPickerEnabled = true,
                    )
                ) {
                    city.points.forEach { point ->
                        val latLngCoords = viewModel.getLatLngCoords(point.coords)

                        Marker(
                            state = rememberMarkerState(position = latLngCoords),
                            icon = getBitmapDescriptorFromVector(
                                LocalContext.current, R.drawable.ic_contacts_orange
                            ),
                            tag = point.id,
                            onClick = {
                                viewModel.changeState(point)
                                false
                            }
                        )
                    }
                }
                PointCard(
                    city.name,
                    viewModel
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PointCard(
    cityName: String,
    viewModel: MapScreenViewModel
) {

    val currentPoint =
        viewModel.currentPointState.collectAsState(initial = viewModel.getStartPoint())
    Log.d("TEST_TEST", "inner rec")

    val swipeableState = rememberSwipeableState(SwipeDirection.Bottom)

    val cardHeight by animateDpAsState(
        targetValue = if (swipeableState.currentValue == SwipeDirection.Bottom) 200.dp else 600.dp,
        label = "Toggle point on map"
    )

    val scope = rememberCoroutineScope()

//    if (swipeableState.isAnimationRunning) {
//        DisposableEffect(Unit) {
//            onDispose {
//                when (swipeableState.currentValue) {
//                    SwipeDirection.Top -> {
//                        println("swipe right")
//                    }
//
//                    SwipeDirection.Bottom -> {
//                        println("swipe left")
//                    }
//
//                    else -> {
//                        return@onDispose
//                    }
//                }
//                scope.launch {
//                    // in your real app if you don't have to display offset,
//                    // snap without animation
//                    // swipeableState.snapTo(SwipeDirection.Initial)
//                    swipeableState.animateTo(SwipeDirection.Bottom)
//                }
//            }
//        }
//    }
    var offsetX by remember { mutableStateOf(100f) }

    Column {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .height(offsetX.pxToDp())
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp
                    )
                )
                .background(Color.White)
                .swipeable(
                    state = swipeableState,
                    anchors = mapOf(
                        0f to SwipeDirection.Top,
                        2f to SwipeDirection.Bottom
                    ),
                    orientation = Orientation.Vertical,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        offsetX -= delta
                    }
                )
        ) {
            Divider(
                modifier = Modifier
                    .padding(
                        start = 180.dp,
                        top = 8.dp,
                        end = 180.dp
                    )
                    .clip(RoundedCornerShape(10.dp)),
                color = Color.LightGray,
                thickness = 4.dp
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cross),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp),
                text = "$cityName-${currentPoint.value.id}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp),
                text = currentPoint.value.address,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp),
                text = "Открыто до 00.00",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = colorResource(R.color.orange).copy(alpha = 0.8f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        bottom = 8.dp,
                        end = 16.dp
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .background(colorResource(R.color.orange)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Заказать здесь", color = Color.White)
            }

        }
    }
}

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


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