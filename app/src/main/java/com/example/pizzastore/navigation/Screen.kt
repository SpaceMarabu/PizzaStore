package com.example.pizzastore.navigation

sealed class Screen(
    val route: String
) {

    data object Home : Screen(ROUTE_HOME)
    data object Menu : Screen(ROUTE_MENU)
    data object Contacts : Screen(ROUTE_CONTACTS)
    data object ChoseCity : Screen(ROUTE_CHOSE_CITY)
    data object MapTakeOut : Screen(ROUTE_MAP_TAKEOUT)
    data object MapDelivery : Screen(ROUTE_MAP_DELIVERY)
    data object Bucket : Screen(ROUTE_BUCKET)
    data object OrderStatus : Screen(ROUTE_ORDER_STATUS)
    data object Order : Screen(ROUTE_ORDER)


    companion object {

        const val KEY_MAP = "map"

        const val ROUTE_MENU = "menu"
        const val ROUTE_CONTACTS = "contacts"
        const val ROUTE_CHOSE_CITY = "city"
        const val ROUTE_MAP_DELIVERY = "map_d"
        const val ROUTE_MAP_TAKEOUT = "map_t"
        const val ROUTE_HOME = "home"
        const val ROUTE_ORDER = "order"
        const val ROUTE_BUCKET = "bucket"
        const val ROUTE_ORDER_STATUS = "order_status"
    }
}
