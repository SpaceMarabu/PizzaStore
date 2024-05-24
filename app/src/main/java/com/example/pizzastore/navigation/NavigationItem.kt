package com.example.pizzastore.navigation

import com.example.pizzastore.R

sealed class NavigationItem(
    val screen: Screen,
    val titleResId: Int,
    val icon: Int
) {

    object Menu : NavigationItem(
        screen = Screen.Menu,
        titleResId = R.string.menu,
        icon = R.drawable.ic_menu
    )

    object Contacts : NavigationItem(
        screen = Screen.Contacts,
        titleResId = R.string.contacts,
        icon = R.drawable.ic_contacts
    )

    object ShoppingBag : NavigationItem(
        screen = Screen.ShoppingBag,
        titleResId = R.string.shopping_bag,
        icon = R.drawable.ic_shopping_bag
    )
}
