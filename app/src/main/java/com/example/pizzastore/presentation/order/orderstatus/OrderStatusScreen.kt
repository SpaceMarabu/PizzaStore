package com.example.pizzastore.presentation.order.orderstatus

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Order

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
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.onLeaveScreen()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        observer.onStateChanged(lifecycleOwner, Lifecycle.Event.ON_DESTROY)

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
                order = currentScreenState.order
            )
        }

        is OrderStatusScreenState.EmptyOrder -> {
            Log.d("WTF", "WTF")

            onOrderIsEmpty()
        }
    }
}

@Composable
fun OrderStatusScreenContent(
    viewModel: OrderStatusScreenViewModel,
    order: Order
) {
    order
}