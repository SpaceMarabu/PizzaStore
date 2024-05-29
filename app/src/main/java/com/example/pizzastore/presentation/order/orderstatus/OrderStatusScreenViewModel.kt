package com.example.pizzastore.presentation.order.orderstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.usecases.GetCurrentOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderStatusScreenViewModel @Inject constructor(
    private val getCurrentOrderUseCase: GetCurrentOrderUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<OrderStatusScreenState>(OrderStatusScreenState.Initial)

    init {
        viewModelScope.launch {
            subscribeCurrentOrderFlow()
        }
    }

    //<editor-fold desc="subscribeCurrentOrderFlow">
    private suspend fun subscribeCurrentOrderFlow() {
        getCurrentOrderUseCase
            .getCurrentOrderFlow()
            .collect { order ->
                if (order != null) {
                    screenState.value = OrderStatusScreenState.Content(order)
                } else {
                    screenState.value = OrderStatusScreenState.EmptyOrder
                }
            }
    }
    //</editor-fold>
}