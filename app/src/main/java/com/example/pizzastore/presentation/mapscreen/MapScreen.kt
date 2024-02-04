package com.example.pizzastore.presentation.mapscreen

import android.Manifest
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.funs.getBitmapDescriptorFromVector
import com.google.android.gms.maps.CameraUpdateFactory
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
    screenState.value

//    val currentPoint =
//        viewModel.currentPointState.collectAsState(initial = viewModel.getStartPoint())

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
                viewModel,
                currentScreenState.currentPoint,
                onPointItemClicked = {
                    viewModel.changeScreenState(currentScreenState.copy(currentPoint = it))
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    city: City,
    viewModel: MapScreenViewModel,
    currentPoint: Point,
    onPointItemClicked: (Point) -> Unit
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
        position = viewModel.getCameraPosition(currentPoint)
    }

    var isButtonShown by remember {
        mutableStateOf(true)
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Box {
        BottomSheetScaffold(
            sheetContent = {
                PointCard(
                    city,
                    currentPoint = currentPoint,
                    onCloseClicked = {
                        isButtonShown = false
                    },
                    onPointItemClicked = {
                        onPointItemClicked(it)
//                        LaunchedEffect(key1 = true) { TODO()
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    viewModel.getCameraPosition(it)
                                ),
                                durationMs = 1000
                            )
                            cameraPositionState.position = viewModel.getCameraPosition(it)
                        }

//                        }

                    }
                )
            },
            scaffoldState = bottomSheetScaffoldState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetPeekHeight = 200.dp,
            sheetGesturesEnabled = true
        ) {
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
//                                            viewModel.changePointState(point)
                                            onPointItemClicked(point)
                                            false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isButtonShown) {
            Column {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                bottom = 16.dp,
                                end = 16.dp
                            )
                            .height(40.dp)
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
    }
}

@Composable
fun PointCard(
    city: City,
    onCloseClicked: () -> Unit,
    currentPoint: Point,
    onPointItemClicked: (Point) -> Unit
) {

    Log.d("TEST_TEST", "inner rec")

    var fullListShowState by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .height(600.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .background(Color.White)
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
        if (!fullListShowState) {
            PointContent(
                cityName = city.name,
                currentPoint = currentPoint,
                onCloseClicked = {
                    fullListShowState = true
                    onCloseClicked()
                }
            )
        } else {
            LazyColumn() {
                var counter = 1
                items(city.points, key = { it.id }) {
                    PointCardForList(
                        cityName = city.name,
                        address = it.address,
                        onPointItemClicked = {
//                            viewModel.changePointState(it)
                            onPointItemClicked(it)
                        }
                    )
                    if (counter < city.points.size) {
                        Divider(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    start = 16.dp
                                ),
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                    counter += 1
                }
            }
        }
    }
}

@Composable
fun PointCardForList(
    cityName: String,
    address: String,
    onPointItemClicked : () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 16.dp
            )
            .clickable {
                onPointItemClicked()
            }
    ) {
        Text(
            text = cityName,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = address,
                fontSize = 16.sp
            )
            Text(
                text = "7.2 км",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}



@Composable
fun PointContent(
    cityName: String,
    currentPoint: Point,
    onCloseClicked: () -> Unit
) {
    val modifierForText = Modifier.padding(top = 8.dp, start = 16.dp)
    Column {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(30.dp)
                        .clickable { onCloseClicked() },
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cross),
                    contentDescription = null
                )
            }
            Text(
                modifier = modifierForText,
                text = "$cityName-${currentPoint.id}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                modifier = modifierForText,
                text = currentPoint.address,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                modifier = modifierForText,
                text = "Открыто до 00.00",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = colorResource(R.color.orange).copy(alpha = 0.8f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TwoTextRow(
                    text1 = "Время работы:",
                    text2 = "08.00 - 00.00",
                    color2 = Color.Gray
                )
                TwoTextRow(
                    text1 = "Телефон:",
                    text2 = "+79990001122",
                    color2 = colorResource(R.color.orange).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun TwoTextRow(
    text1: String,
    text2: String,
    color1: Color = Color.Unspecified,
    color2: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text1,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.SansSerif,
            color = color1
        )
        Text(
            text = text2,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            color = color2
        )
    }
}
