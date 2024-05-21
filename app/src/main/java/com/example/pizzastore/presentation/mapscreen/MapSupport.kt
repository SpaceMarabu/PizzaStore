package com.example.pizzastore.presentation.mapscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.pizzastore.R
import com.example.pizzastore.presentation.mapscreen.takeout.TakeoutMapScreenViewModel
import com.example.pizzastore.presentation.mapscreen.takeout.ZoomDirection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

//<editor-fold desc="ChangeMapZoom">
@Composable
fun ChangeMapZoom(
    viewModel: TakeoutMapScreenViewModel,
    cameraPositionState: CameraPositionState,
    scope: CoroutineScope,
    zoomState: MutableState<ZoomDirection>
) {
    val zoom = zoomState.value
    val newCameraPosition = viewModel.getNewCameraPosition(zoom)
    ChangeMapPosition(
        cameraPositionState = cameraPositionState,
        newCameraPosition = newCameraPosition,
        scope = scope
    )
    zoomState.value = ZoomDirection.Nothing
}
//</editor-fold>

//<editor-fold desc="ChangeMapPosition">
@Composable
fun ChangeMapPosition(
    cameraPositionState: CameraPositionState,
    newCameraPosition: CameraPosition,
    scope: CoroutineScope
) {
    LaunchedEffect(key1 = true) {
        scope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    newCameraPosition
                ),
                durationMs = 500
            )
            cameraPositionState.position = newCameraPosition
        }
    }
}
//</editor-fold>

//<editor-fold desc="RowWithIcon">
@Composable
fun RowWithIcon(id: Int, onClick: () -> Unit) {
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onClick() },
                imageVector = ImageVector.vectorResource(id = id),
                contentDescription = null
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="RequestPermissionsButton">
@Composable
fun RequestPermissionsButton(
    onButtonClicked: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            onButtonClicked()
        }) {
            Text(text = stringResource(R.string.get_permission_button))
        }
    }
}
//</editor-fold>



