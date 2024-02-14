package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.homeScreenNavGraph(
    menuScreenContent: @Composable () -> Unit,
    choseCityScreenContent: @Composable () -> Unit,
    takeOutMapScreenContent: @Composable () -> Unit,
    deliveryMapScreenContent: @Composable () -> Unit
) {
    navigation(
        startDestination = Screen.Menu.route,
        route = Screen.Home.route
    ) {
        composable(Screen.Menu.route) {
            menuScreenContent()
        }
        composable(Screen.ChoseCity.route) {
            choseCityScreenContent()
        }
        composable(Screen.MapTakeOut.route) {
            takeOutMapScreenContent()
        }
        composable(Screen.MapDelivery.route) {
            deliveryMapScreenContent()
        }
    }
}
