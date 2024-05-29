package com.example.pizzastore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.orderScreenNavGraph(
    bucketScreenContent: @Composable () -> Unit,
    orderStatusScreenContent: @Composable () -> Unit,
) {
    navigation(
        startDestination = Screen.OrderStatus.route,
        route = Screen.Order.route
    ) {
        composable(Screen.Bucket.route) {
            bucketScreenContent()
        }
        composable(Screen.OrderStatus.route) {
            orderStatusScreenContent()
        }
    }
}
