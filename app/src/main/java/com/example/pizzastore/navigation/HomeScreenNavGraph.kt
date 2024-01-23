package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.pizzastore.domain.entity.City

fun NavGraphBuilder.homeScreenNavGraph(
    menuScreenContent: @Composable () -> Unit,
    choseCityScreenContent: @Composable () -> Unit,
    mapScreenContent: @Composable (points: String) -> Unit
) {
    navigation(
        startDestination = Screen.ChoseCity.route,
        route = Screen.Home.route
    ) {
        composable(Screen.Menu.route) {
            menuScreenContent()
        }
        composable(Screen.ChoseCity.route) {
            choseCityScreenContent()
        }
        composable(Screen.Map.route) {
            val points = it.arguments?.getString(Screen.KEY_MAP) ?: ""
            mapScreenContent(points)
        }
    }
}
