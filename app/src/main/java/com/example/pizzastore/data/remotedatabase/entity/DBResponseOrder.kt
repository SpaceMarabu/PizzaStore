package com.example.pizzastore.data.remotedatabase.entity

sealed class DBResponseOrder {

    data object Error : DBResponseOrder()
    data class Complete(val orderId: Int) : DBResponseOrder()
    data object Processing : DBResponseOrder()
    data object Initial : DBResponseOrder()
}
