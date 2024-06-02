package com.example.pizzastore.presentation.order.orderstatus

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.OrderStatus

@Composable
fun OrderStatusScreen(
    paddingValues: PaddingValues,
    onOrderIsEmpty: () -> Unit
) {

    val component = getApplicationComponent()
    val viewModel: OrderStatusScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState by viewModel.screenState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.onLeaveScreen()
                }

                Lifecycle.Event.ON_CREATE -> {
                    Log.d("WTF", "WTF")
                }

                Lifecycle.Event.ON_START -> {
                    Log.d("WTF", "WTF")
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d("WTF", "WTF")
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("WTF", "WTF")
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("WTF", "WTF")
                }

                else -> {}
            }
        }

//        ProcessLifecycleOwner.get().getLifecycle()

        lifecycleOwner.lifecycle.addObserver(observer)

//        observer.onStateChanged(lifecycleOwner, Lifecycle.Event.ON_DESTROY)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (screenState) {
        is OrderStatusScreenState.Initial -> {}
        is OrderStatusScreenState.Content -> {
            val currentScreenState = screenState as OrderStatusScreenState.Content
            OrderStatusScreenContent(
                viewModel = viewModel,
                order = currentScreenState.order,
                paddingValues = paddingValues
            )
        }

        is OrderStatusScreenState.EmptyOrder -> {
            onOrderIsEmpty()
            viewModel.onLeaveScreen()
        }
    }
}

@Composable
fun OrderStatusScreenContent(
    viewModel: OrderStatusScreenViewModel,
    order: Order,
    paddingValues: PaddingValues
) {
    val currentOrderStatusDesc = when (order.status) {
        OrderStatus.NEW -> "Создан"
        OrderStatus.PROCESSING -> "В работе"
        OrderStatus.FINISH -> "Завершен"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues.calculateBottomPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Заказ №${order.id}")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = currentOrderStatusDesc)
        }
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
        ) {
            order.bucket.order.forEach { itemProduct ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(text = "${itemProduct.key.name}: ${itemProduct.value}")
                    }
                }
            }
        }
    }
}