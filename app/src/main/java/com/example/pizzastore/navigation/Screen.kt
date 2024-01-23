package com.example.pizzastore.navigation

import android.net.Uri
import com.example.pizzastore.domain.entity.City
import com.google.gson.Gson
import okio.ByteString.Companion.encode

sealed class Screen(
    val route: String
) {

    object Home : Screen(ROUTE_HOME)
    object Menu : Screen(ROUTE_MENU)
    object Contacts : Screen(ROUTE_CONTACTS)
    object Profile : Screen(ROUTE_PROFILE)
    object ShoppingBag : Screen(ROUTE_SHOPPING_BAG)
    object ChoseCity : Screen(ROUTE_CHOSE_CITY)
    object Map : Screen(ROUTE_MAP) {
        private const val ROUTE_FOR_ARGS = "map"

        fun getRouteWithArgs(points: String): String {
            "$ROUTE_FOR_ARGS/${points}"
            return "$ROUTE_FOR_ARGS/${points}"
        }
    }


    companion object {

        const val KEY_MAP = "map"

        const val ROUTE_MENU = "menu"
        const val ROUTE_PROFILE = "profile"
        const val ROUTE_CONTACTS = "contacts"
        const val ROUTE_SHOPPING_BAG = "shopping_bag"
        const val ROUTE_CHOSE_CITY = "city"
        const val ROUTE_MAP = "map/{$KEY_MAP}"
        const val ROUTE_HOME = "home"
    }
}

fun String.encode(): String {
    return Uri.encode(this)
}
