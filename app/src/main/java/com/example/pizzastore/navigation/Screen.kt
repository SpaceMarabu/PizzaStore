package com.example.pizzastore.navigation

sealed class Screen(
    val route: String
) {

    object Menu : Screen(ROUTE_MENU)
    object Contacts : Screen(ROUTE_CONTACTS)
    object Profile : Screen(ROUTE_PROFILE)
    object ShoppingBag : Screen(ROUTE_SHOPPING_BAG)


    companion object {

        const val ROUTE_MENU = "menu"
        const val ROUTE_PROFILE = "profile"
        const val ROUTE_CONTACTS = "contacts"
        const val ROUTE_SHOPPING_BAG = "shopping_bag"
    }
}
