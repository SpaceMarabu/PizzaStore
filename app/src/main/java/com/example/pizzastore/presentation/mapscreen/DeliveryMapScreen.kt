package com.example.pizzastore.presentation.mapscreen

import android.Manifest
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.funs.dpToPx
import com.example.pizzastore.presentation.funs.pxToDp
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun DeliveryMapScreen(paddingValues: PaddingValues) {

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
            DeliveryMapScreenContent(
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
fun DeliveryMapScreenContent(
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

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()


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
                    Column {
                        val displayMetrics: DisplayMetrics =
                            LocalContext.current.resources.displayMetrics
                        val displayWithoutBottomContent = displayMetrics.heightPixels.toFloat().pxToDp() - 350.dp
                        Log.d("TEST_TEST", (500.dp.dpToPx()).toString())

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
//                                Log.d("TEST_TEST_camera", cameraPositionState.position.toString())
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
                                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(Color.LightGray.copy(alpha = 0.5f)),
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
                }
            }
        }
    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center
//    ) {
//        val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
//
//        if (bottomSheetState.isCollapsed && !bottomSheetState.isAnimationRunning) {
//            Row {
//                Spacer(modifier = Modifier.weight(1f))
//                Box(
//                    modifier = Modifier
//                        .padding(end = 12.dp)
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(50.dp))
//                        .background(Color.White)
//                ) {
//                    Icon(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clickable { },
//                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_compass_row),
//                        contentDescription = null
//                    )
//                }
//            }
//        }
//    }
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
