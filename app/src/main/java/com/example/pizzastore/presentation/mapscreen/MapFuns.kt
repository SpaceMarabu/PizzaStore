package com.example.pizzastore.presentation.mapscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChangeMapZoom(
    viewModel: MapScreenViewModel,
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