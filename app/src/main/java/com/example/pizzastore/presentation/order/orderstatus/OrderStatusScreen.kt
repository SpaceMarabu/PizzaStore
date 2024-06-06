package com.example.pizzastore.presentation.order.orderstatus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
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
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.onLeaveScreen()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

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
            DisposableEffect(Unit) {
                onOrderIsEmpty()

                onDispose {
                    viewModel.onLeaveScreen()
                }
            }
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
        else -> "Принят"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Заказ №${order.id}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentOrderStatusDesc,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Column {
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
            ) {
                order.bucket.order.forEach { itemProduct ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, top = 8.dp)
                        ) {
                            Text(
                                text = "${itemProduct.key.name}: ${itemProduct.value} шт.",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            if (order.status == OrderStatus.FINISH) {
                AcceptButton {
                    viewModel.acceptOrder()
                }
            }
        }
    }
}

//<editor-fold desc="AcceptButton">
@Composable
fun AcceptButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp
            )
            .height(58.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                )
                .height(50.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(colorResource(R.color.orange))
                .clickable {
                    onClick()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Всё круто!",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        }
    }
}
//</editor-fold>