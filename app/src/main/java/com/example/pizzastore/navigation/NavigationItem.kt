package com.example.pizzastore.navigation

import com.example.pizzastore.R

sealed class NavigationItem(
    val screen: Screen,
    val icon: Int
) {

    data object Menu : NavigationItem(
        screen = Screen.Menu,
        titleResId = R.string.menu,
        icon = R.drawable.ic_menu
    )

    data object Contacts : NavigationItem(
        screen = Screen.Contacts,
        titleResId = R.string.contacts,
        icon = R.drawable.ic_mail
    )

    data object ShoppingBag : NavigationItem(
        screen = Screen.Order,
        titleResId = R.string.order,
        icon = R.drawable.ic_shopping_bag
    )
}
