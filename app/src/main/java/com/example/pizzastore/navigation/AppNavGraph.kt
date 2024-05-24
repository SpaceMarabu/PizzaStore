package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    menuScreenContent: @Composable () -> Unit,
    contactsScreenContent: @Composable () -> Unit,
    choseCityScreenContent: @Composable () -> Unit,
    takeOutMapScreenContent: @Composable () -> Unit,
    deliveryMapScreenContent: @Composable () -> Unit,
    bucketScreenContent: @Composable () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route
    ) {
        homeScreenNavGraph(
            menuScreenContent = menuScreenContent,
            choseCityScreenContent = choseCityScreenContent,
            takeOutMapScreenContent = takeOutMapScreenContent,
            deliveryMapScreenContent = deliveryMapScreenContent
        )
        composable(Screen.Contacts.route) {
            contactsScreenContent()
        }
        composable(Screen.Bucket.route) {
         bucketScreenContent()
        }
    }
}
