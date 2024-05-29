package com.example.pizzastore.data.remotedatabase.entity

sealed class DBResultOrder {

    data object Error : DBResultOrder()
    data class Complete(val orderId: Int) : DBResultOrder()
}
