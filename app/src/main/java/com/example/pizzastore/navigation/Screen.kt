package com.example.pizzastore.navigation

sealed class Screen(
    val route: String
) {

    object Home : Screen(ROUTE_HOME)
    object Menu : Screen(ROUTE_MENU)
    object Contacts : Screen(ROUTE_CONTACTS)
    object ChoseCity : Screen(ROUTE_CHOSE_CITY)
    object MapTakeOut : Screen(ROUTE_MAP_TAKEOUT)
    object MapDelivery : Screen(ROUTE_MAP_DELIVERY)
    object Bucket : Screen(ROUTE_BUCKET)


    companion object {

        const val KEY_MAP = "map"

        const val ROUTE_MENU = "menu"
        const val ROUTE_CONTACTS = "contacts"
        const val ROUTE_CHOSE_CITY = "city"
        const val ROUTE_MAP_DELIVERY = "map_d"
        const val ROUTE_MAP_TAKEOUT = "map_t"
        const val ROUTE_HOME = "home"
        const val ROUTE_BUCKET = "bucket"
    }
}
