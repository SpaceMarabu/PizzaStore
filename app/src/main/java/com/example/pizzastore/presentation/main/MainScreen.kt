package com.example.pizzastore.presentation.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pizzastore.navigation.AppNavGraph
import com.example.pizzastore.navigation.NavigationItem
import com.example.pizzastore.navigation.Screen
import com.example.pizzastore.navigation.rememberNavigationState
import com.example.pizzastore.presentation.chosecity.ChoseCityScreen
import com.example.pizzastore.presentation.mapscreen.MapScreen
import com.example.pizzastore.presentation.menu.MenuScreen


@Composable
fun MainScreen() {


    val navigationState = rememberNavigationState()

    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    val screensWithBottom = listOf(
        Screen.ROUTE_MENU,
        Screen.ROUTE_CONTACTS,
        Screen.ROUTE_PROFILE,
        Screen.ROUTE_SHOPPING_BAG
    )

    val showBottomBar = navBackStackEntry?.destination?.route in screensWithBottom

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation {

                    val items = listOf(
                        NavigationItem.Menu,
                        NavigationItem.Profile,
                        NavigationItem.Contacts,
                        NavigationItem.ShoppingBag,
                        )
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { item ->

                        BottomNavigationItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navigationState.navigateTo(item.screen.route)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    imageVector = ImageVector.vectorResource(item.icon),
                                    contentDescription = null
                                )
                            },
                            selectedContentColor = MaterialTheme.colors.onPrimary,
                            unselectedContentColor = MaterialTheme.colors.onSecondary
                        )
                    }
                }
            }

        }
    ) {paddingValues ->

        AppNavGraph(
            navHostController = navigationState.navHostController,
            menuScreenContent = {
                    MenuScreen(
                        onCityClick = {
                            navigationState.navigateTo(Screen.ROUTE_CHOSE_CITY)
                        },
                        onAddressClick = {
                            navigationState.navigateTo(Screen.ROUTE_MAP)
            //                        when (cityState.deliveryType) {
            //                            DeliveryType.TAKE_OUT -> TODO()
            //                            DeliveryType.DELIVERY_TO -> TODO()
            //                        }
                        },
                        onCityIsEmpty = {
                            navigationState.navigateTo(Screen.ROUTE_CHOSE_CITY)
                        }
                    )
            },
            profileScreenContent = { Text(text = "profile") },
            contactsScreenContent = { Text(text = "contacts") },
            shoppingBagScreenContent = { Text(text = "shoppingBag") },
            choseCityScreenContent = {
                ChoseCityScreen {
                    navigationState.navigateWithDestroy(Screen.ROUTE_MENU)
                }
            },
            mapScreenContent = {
                    MapScreen(paddingValues = paddingValues)
            }
        )
    }
}

