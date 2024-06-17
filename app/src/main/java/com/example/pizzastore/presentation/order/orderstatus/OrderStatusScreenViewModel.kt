package com.example.pizzastore.presentation.order.orderstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.usecases.AcceptOrderUseCase
import com.example.pizzastore.domain.usecases.GetCurrentOrderUseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderStatusScreenViewModel @Inject constructor(
    private val getCurrentOrderUseCase: GetCurrentOrderUseCase,
    private val acceptOrderUseCase: AcceptOrderUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<OrderStatusScreenState>(OrderStatusScreenState.Initial)
    private val scope = viewModelScope

    init {
        scope.launch {
            subscribeCurrentOrderFlow()
        }
    }

    //<editor-fold desc="subscribeCurrentOrderFlow">
    private suspend fun subscribeCurrentOrderFlow() {
        getCurrentOrderUseCase
            .getCurrentOrderFlow()
            .stateIn(scope)
            .collect { order ->
                if (order != null) {
                    screenState.emit(OrderStatusScreenState.Content(order))
                } else {
                    screenState.emit(OrderStatusScreenState.EmptyOrder)
                }
            }
    }
    //</editor-fold>

    //<editor-fold desc="onLeaveScreen">
    fun onLeaveScreen() {
        screenState.value = OrderStatusScreenState.Initial
        scope.cancel()
    }
    //</editor-fold>

    //<editor-fold desc="acceptOrder">
    fun acceptOrder() = acceptOrderUseCase.acceptOrder()
    //</editor-fold>
}