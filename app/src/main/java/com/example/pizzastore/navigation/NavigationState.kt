package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateToMenu(cityId: String) {
        navHostController.navigate(Screen.Menu.getRouteWithArgs(cityId))
    }

    fun navigateTo(route: String) {
        navHostController.navigate(route) {
            popUpTo(Screen.ROUTE_MENU) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState {
    return remember {
        NavigationState(navHostController)
    }
}