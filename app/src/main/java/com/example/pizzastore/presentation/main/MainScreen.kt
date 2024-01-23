package com.example.pizzastore.presentation.main

import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.navigation.AppNavGraph
import com.example.pizzastore.navigation.NavigationItem
import com.example.pizzastore.navigation.Screen
import com.example.pizzastore.navigation.rememberNavigationState
import com.example.pizzastore.presentation.chosecity.ChoseCityScreen
import com.example.pizzastore.presentation.chosecity.CityDeliveryViewModel
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.presentation.menu.MenuScreen
import com.example.pizzastore.presentation.menu.MenuScreenViewModel
import com.google.gson.Gson


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {


    val navigationState = rememberNavigationState()

    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

//    val screenState: State<MainScreenState> = viewModel.state.collectAsState()



//    when (screenState.value) {
//        is MainScreenState.Initial -> {}
//
//        is MainScreenState.City -> {
//            cityState = viewModel.defaultCity
//        }
//        is MainScreenState.Loading -> { CircularLoading() }
//    }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    NavigationItem.Menu,
                    NavigationItem.Profile,
                    NavigationItem.Contacts,
                    NavigationItem.ShoppingBag,

                    )
                items.forEach { item ->

//                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
//                        it.route == item.screen.route
//                    } ?: false
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
    ) {


        AppNavGraph(
            navHostController = navigationState.navHostController,
            menuScreenContent = {
                    MenuScreen(
                        onCityClick = {
                            navigationState.navigateTo(Screen.ROUTE_CHOSE_CITY)
                        },
                        onAddressClick = {points ->
                            navigationState.navigateToMap(points)
            //                        when (cityState.deliveryType) {
            //                            DeliveryType.TAKE_OUT -> TODO()
            //                            DeliveryType.DELIVERY_TO -> TODO()
            //                        }
                        }
                    )
            },
            profileScreenContent = { Text(text = "profile") },
            contactsScreenContent = { Text(text = "contacts") },
            shoppingBagScreenContent = { Text(text = "shoppingBag") },
            choseCityScreenContent = {
                ChoseCityScreen {
//                    navigationState.navigateToMenu(it.id.toString())
                    navigationState.navigateTo(Screen.ROUTE_MENU)
                }
            },
            mapScreenContent = {points ->
                if (points != null) {
                    MapScreen(points)
                }
            }
        )
    }
}

