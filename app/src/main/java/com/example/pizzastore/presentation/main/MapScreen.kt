package com.example.pizzastore.presentation.main

import android.Manifest
import android.media.metrics.Event
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.pizzastore.BuildConfig
import com.example.pizzastore.R
import com.example.pizzastore.domain.entity.MapPoint
import com.example.pizzastore.domain.entity.MapPosition
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider


@Composable
fun MapScreen() {

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

//    Button(
//        enabled = !permissionGranted, // if the permission is NOT granted, enable the button
//        onClick = {
//            if (!permissionGranted) {
//                // ask for permission
//                permissionLauncher.launch(permissions)
//            }
//        }) {
//        Text(text = if (permissionGranted) "Permission Granted" else "Enable Permission")
//    }


//    MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
//    MapKitFactory.setApiKey("02f63f6b-81ce-41bc-a6cc-74419866b012")


    MapKitFactory.initialize(LocalContext.current)

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                MapKitFactory.getInstance().onStart()
            }

            Lifecycle.Event.ON_STOP -> {
                MapKitFactory.getInstance().onStop()
            }

            else -> {}
        }
    }

    val positionSaver = Saver<CameraPosition, MapPosition>(
        save = {
            val item = it
            val target = it.target
            MapPosition(
                MapPoint(
                    target.latitude,
                    target.longitude
                ),
                zoom = it.zoom,
                azimuth = it.azimuth,
                tilt = it.tilt
            )
        },
        restore = {
            val position = it
            CameraPosition(
                Point(it.target.x, it.target.y),
                it.zoom,
                it.azimuth,
                it.tilt
            )
        }
    )

    var cameraPositionState = rememberSaveable (
        saver = positionSaver
    ) {
        CameraPosition(
            Point(55.751225, 37.629540),
            /* zoom = */ 17.0f,
            /* azimuth = */ 150.0f,
            /* tilt = */ 30.0f
        )
    }



    if (permissionGranted) {
        AndroidView(

            modifier = Modifier.fillMaxSize(),

            factory = { context ->
                // Создаем вью
                MapView(context).apply {
                    map.move(
                        cameraPositionState
                    )
                    val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_contacts)
                    val mapObjects = this.map.mapObjects.addCollection()
                    mapObjects.addPlacemark().apply {
                        geometry = Point(55.751225, 37.629540)
                        setIcon(imageProvider)
                        isVisible = true
                    }
                }
            },

            update = { view ->

                // К view был применен механизм надувания,
                // или обновилось состояние чтения в этом блоке

                // Поскольку selectedItem читается здесь,
                // AndroidView рекомпозируется
                // вне зависимости от изменений состояния
            }
        )
    }
}

//fun pointSaver() {
//    return Saver<MutableState<Point>, Any> = listSaver(
//        save = {
//            val testData = it.value
//            listOf(testData.number, testData.text)
//        },
//        restore = {
//            val testData = TestData(
//                number = it[0] as Int,
//                text = it[1] as String
//            )
//            mutableStateOf(testData)
//        }
//    )
//}

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