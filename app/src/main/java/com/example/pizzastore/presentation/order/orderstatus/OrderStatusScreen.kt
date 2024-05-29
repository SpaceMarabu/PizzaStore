package com.example.pizzastore.presentation.order.orderstatus

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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