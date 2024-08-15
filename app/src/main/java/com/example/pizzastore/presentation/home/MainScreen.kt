package com.example.pizzastore.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pizzastore.navigation.AppNavGraph
import com.example.pizzastore.navigation.NavigationItem
import com.example.pizzastore.navigation.Screen
import com.example.pizzastore.navigation.rememberNavigationState
import com.example.pizzastore.presentation.chosecity.ChoseCityScreen
import com.example.pizzastore.presentation.contacts.ContactsScreen
import com.example.pizzastore.presentation.mapscreen.delivery.DeliveryMapScreen
import com.example.pizzastore.presentation.mapscreen.takeout.TakeOutMapScreen
import com.example.pizzastore.presentation.menu.MenuScreen
import com.example.pizzastore.presentation.order.bucket.BucketScreen
import com.example.pizzastore.presentation.order.orderstatus.OrderStatusScreen


@Composable
fun MainScreen() {

    val navigationState = rememberNavigationState()

    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    val screensWithBottom = listOf(
        Screen.ROUTE_MENU,
        Screen.ROUTE_CONTACTS,
        Screen.ROUTE_ORDER_STATUS,
        Screen.ROUTE_BUCKET
    )

    val showBottomBar = navBackStackEntry?.destination?.route in screensWithBottom

    val items = listOf(
        NavigationItem.Menu,
        NavigationItem.Contacts,
        NavigationItem.ShoppingBag,
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.shadow(elevation = 12.dp)
                ) {

                    items.forEach { item ->

                        val selected = navBackStackEntry?.destination?.hierarchy?.any {
                            it.route == item.screen.route
                        } ?: false

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navigationState.navigateStartDestination(item.screen.route)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(40.dp),
                                    imageVector = ImageVector.vectorResource(item.icon),
                                    contentDescription = null
                                )
                            },
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                selectedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }

        }
    ) { paddingValues ->

        AppNavGraph(
            navHostController = navigationState.navHostController,
            menuScreenContent = {
                MenuScreen(
                    paddingValues = paddingValues,
                    onCityClick = {
                        navigationState.navigateTo(Screen.ROUTE_CHOSE_CITY)
                    },
                    onAddressClick = { isTakeout ->
                        if (isTakeout) {
                            navigationState.navigateTo(Screen.ROUTE_MAP_TAKEOUT)
                        } else {
                            navigationState.navigateTo(Screen.ROUTE_MAP_DELIVERY)
                        }
                    },
                    onCityIsEmpty = {
                        navigationState.navigateTo(Screen.ROUTE_CHOSE_CITY)
                    }
                )
            },
            contactsScreenContent = {
                ContactsScreen(paddingValues = paddingValues)
            },
            choseCityScreenContent = {
                ChoseCityScreen {
                    navigationState.navigateWithDestroy(Screen.ROUTE_MENU)
                }
            },
            takeOutMapScreenContent = {
                TakeOutMapScreen(paddingValues = paddingValues) {
                    navigationState.navigateTo(Screen.ROUTE_MENU)
                }
            },
            deliveryMapScreenContent = {
                DeliveryMapScreen() {
                    navigationState.navigateTo(Screen.ROUTE_MENU)
                }
            },
            orderStatusScreenContent = {
                OrderStatusScreen(paddingValues = paddingValues) {
                    navigationState.navigateWithFromOrderStatus(Screen.ROUTE_BUCKET)
                }
            },
            bucketScreenContent = {
                BucketScreen(paddingValues = paddingValues) {
                    navigationState.navigateStartDestination(Screen.ROUTE_ORDER)
                }
            }
        )
    }
}

