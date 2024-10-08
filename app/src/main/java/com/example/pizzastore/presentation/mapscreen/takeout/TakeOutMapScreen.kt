package com.example.pizzastore.presentation.mapscreen.takeout

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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
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
import com.example.pizzastore.presentation.utils.CircularLoading
import com.example.pizzastore.presentation.utils.getBitmapDescriptorFromVector
import com.example.pizzastore.presentation.mapscreen.ChangeMapPosition
import com.example.pizzastore.presentation.mapscreen.ChangeMapZoom
import com.example.pizzastore.presentation.mapscreen.RequestPermissionsButton
import com.example.pizzastore.presentation.mapscreen.RowWithIcon
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun TakeOutMapScreen(
    paddingValues: PaddingValues,
    onOrderButtonClicked: () -> Unit
) {

    val component = getApplicationComponent()
    val viewModel: TakeoutMapScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState()

    when (screenState.value) {
        is TakeoutMapScreenState.Initial -> {}
        is TakeoutMapScreenState.Loading -> {
            CircularLoading()
        }

        is TakeoutMapScreenState.Content -> {
            val currentScreenState = screenState.value as TakeoutMapScreenState.Content
            TakeOutMapScreenContent(
                paddingValues = paddingValues,
                currentScreenState.city,
                viewModel,
                currentScreenState.currentPoint,
                onPointItemClicked = {
                    viewModel.changeScreenState(currentScreenState.copy(currentPoint = it))
                },
                onOrderButtonClicked = {
                    viewModel.pointChoose()
                    onOrderButtonClicked()
                }
            )
        }
    }
}

//<editor-fold desc="Экран с контентом">
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeOutMapScreenContent(
    paddingValues: PaddingValues,
    city: City,
    viewModel: TakeoutMapScreenViewModel,
    currentPoint: Point,
    onPointItemClicked: (Point) -> Unit,
    onOrderButtonClicked: () -> Unit
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
        position = viewModel.getCameraPosition(currentPoint)
    }

    val scope = rememberCoroutineScope()
    var isPointChanged by remember {
        mutableStateOf(false)
    }
    if (isPointChanged) {
        val newCameraPosition = viewModel.getNewCameraPosition()
        ChangeMapPosition(
            cameraPositionState = cameraPositionState,
            newCameraPosition = newCameraPosition,
            scope = scope
        )
        isPointChanged = false
    }

    val zoomChangeState: MutableState<ZoomDirection> = remember {
        mutableStateOf(ZoomDirection.Nothing)
    }

    when (zoomChangeState.value) {
        ZoomDirection.Minus -> {
            ChangeMapZoom(
                viewModel = viewModel,
                cameraPositionState = cameraPositionState,
                scope = scope,
                zoomState = zoomChangeState
            )
        }

        ZoomDirection.Nothing -> {}
        ZoomDirection.Plus -> {
            ChangeMapZoom(
                viewModel = viewModel,
                cameraPositionState = cameraPositionState,
                scope = scope,
                zoomState = zoomChangeState
            )
        }
    }

    var isButtonShown by remember {
        mutableStateOf(true)
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            density = LocalDensity.current
        )
    )
    val bottomSheetState = bottomSheetScaffoldState.bottomSheetState

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
                        isButtonShown = true
                        isPointChanged = true
                        viewModel.changePoint(it)
                    }
                )
            },
//            scaffoldState = bottomSheetScaffoldState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetPeekHeight = 200.dp,
            sheetDragHandle = {}
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
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (permissionGranted) {
                        Box {
                            CustomGoogleMap(
                                cameraPositionState = cameraPositionState,
                                city = city,
                                viewModel = viewModel
                            ) {
                                onPointItemClicked(it)
                            }
                        }
                    } else {
                        RequestPermissionsButton {
                            needRequestPermission = true
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (bottomSheetState.currentValue != SheetValue.Expanded
                && bottomSheetState.hasPartiallyExpandedState
                ) {
                RowWithIcon(id = R.drawable.ic_plus) {
                    zoomChangeState.value = ZoomDirection.Plus
                }
                Spacer(modifier = Modifier.size(8.dp))
                RowWithIcon(id = R.drawable.ic_minus) {
                    zoomChangeState.value = ZoomDirection.Minus
                }
            }
        }
        if (isButtonShown) {
            OrderButton {
                onOrderButtonClicked()
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="Карта">
@Composable
fun CustomGoogleMap(
    cameraPositionState: CameraPositionState,
    city: City,
    viewModel: TakeoutMapScreenViewModel,
    onPointItemClicked: (Point) -> Unit
) {
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
                    onPointItemClicked(point)
                    false
                }
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="Кнопка заказать">
@Composable
fun OrderButton(onOrderButtonClicked: () -> Unit) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .height(height = 80.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(colorResource(R.color.orange))
                    .clickable {
                        onOrderButtonClicked()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Заказать здесь", color = Color.White)
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="bottom Sheet, без кнопки заказать">
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

        HorizontalDivider(
            modifier = Modifier
                .padding(
                    start = 180.dp,
                    top = 8.dp,
                    end = 180.dp
                )
                .clip(RoundedCornerShape(10.dp)),
            thickness = 4.dp,
            color = Color.LightGray
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
                            fullListShowState = false
                        }
                    )
                    if (counter < city.points.size) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    start = 16.dp
                                ),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }
                    counter += 1
                }
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="Карточка пиццерии для списка">
@Composable
fun PointCardForList(
    cityName: String,
    address: String,
    onPointItemClicked: () -> Unit
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
//</editor-fold>

//<editor-fold desc="PointContent">
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
//</editor-fold>

//<editor-fold desc="TwoTextRow">
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
//</editor-fold>
