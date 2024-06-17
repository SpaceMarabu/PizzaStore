package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(route: String) {
        navHostController.navigate(route) {
            popUpTo(Screen.ROUTE_MENU) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateWithDestroy(route: String) {
        navHostController.popBackStack()
        navHostController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateStartDestination(route: String) {
        navHostController.navigate(route) {
            launchSingleTop = true
            popUpTo(0)
        }
    }

    fun navigateWithFromOrderStatus(route: String) {
        navHostController.navigate(route) {
            popUpTo(Screen.ROUTE_ORDER) { inclusive = true }
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