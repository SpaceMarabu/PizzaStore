package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.presentation.main.MainScreenState

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    menuScreenContent: @Composable (String?) -> Unit,
    profileScreenContent: @Composable () -> Unit,
    contactsScreenContent: @Composable () -> Unit,
    shoppingBagScreenContent: @Composable () -> Unit,
    choseCityScreenContent: @Composable () -> Unit,
    mapScreenContent: @Composable () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route
    ) {
        homeScreenNavGraph(
            menuScreenContent = menuScreenContent,
            choseCityScreenContent = choseCityScreenContent,
            mapScreenContent = mapScreenContent
        )
        composable(Screen.Profile.route) {
            profileScreenContent()
        }
        composable(Screen.Contacts.route) {
            contactsScreenContent()
        }
        composable(Screen.ShoppingBag.route) {
            shoppingBagScreenContent()
        }
    }
}
