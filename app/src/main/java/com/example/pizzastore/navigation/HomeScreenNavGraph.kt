package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.pizzastore.domain.entity.City

fun NavGraphBuilder.homeScreenNavGraph(
    menuScreenContent: @Composable (cityId: String?) -> Unit,
    choseCityScreenContent: @Composable () -> Unit,
    mapScreenContent: @Composable () -> Unit
) {
    navigation(
        startDestination = Screen.Menu.route,
        route = Screen.Home.route
    ) {
        composable(
            route = Screen.Menu.route,
            arguments = listOf(
                navArgument(Screen.KEY_CITY) {
                    type = City.NavigationType
                }
            )
        ) {
            val cityId = it.arguments?.getString(Screen.KEY_CITY)
            menuScreenContent(cityId)
        }
        composable(Screen.ChoseCity.route) {
            choseCityScreenContent()
        }
        composable(Screen.Map.route) {
            mapScreenContent()
        }
    }
}
