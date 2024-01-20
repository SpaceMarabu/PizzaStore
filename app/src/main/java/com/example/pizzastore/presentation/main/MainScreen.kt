package com.example.pizzastore.presentation.main

import android.annotation.SuppressLint
import android.util.DisplayMetrics
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzastore.R
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.navigation.AppNavGraph
import com.example.pizzastore.navigation.NavigationItem
import com.example.pizzastore.navigation.Screen
import com.example.pizzastore.presentation.chosecity.ChoseCityScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {


    val navHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
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
                            navHostController.navigate(item.screen.route) {
                                popUpTo(Screen.ROUTE_MENU) {
                                    saveState = true
                                }
//                                anim {
//                                    enter = android.R.animator.fade_in
//                                    exit = android.R.animator.fade_out
//                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
        val paddingStart = 16.dp
        val paddingTop = 16.dp

        var cityState by rememberSaveable {
            mutableStateOf(City())
        }

        AppNavGraph(
            navHostController = navHostController,
            menuScreenContent = {
                MenuScreenContent(
                    paddingStart = paddingStart,
                    paddingTop = paddingTop,
                    city = cityState,
                    onCityClick = {
                        navHostController.navigate(Screen.ROUTE_CHOSE_CITY)
                    },
                    onDeliveryClick = {
                        cityState = cityState.copy(deliveryType = it)
                    },
                    onAddressClick = {
                        navHostController.navigate(Screen.ROUTE_MAP)
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
                    cityState = it
                    navHostController.popBackStack()
                }
            },
            mapScreenContent = { MapScreen() }
        )
    }
}

@Composable
fun MenuScreenContent(
    city: City,
    paddingStart: Dp,
    paddingTop: Dp,
    onCityClick: () -> Unit,
    onDeliveryClick: (DeliveryType) -> Unit,
    onAddressClick: () -> Unit
) {
    Column {
        ChoseCity(
            city.name,
            paddingStart,
            paddingTop,
            onCityClick
        )
        ChoseDeliveryType(
            city = city,
            onDeliveryClick = {
                onDeliveryClick(it)
            },
            onAddressClick = {
                onAddressClick()
            }
        )
    }

}

@Composable
fun ChoseDeliveryType(
    city: City,
    onDeliveryClick: (DeliveryType) -> Unit,
    onAddressClick: () -> Unit
) {
    Column (
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(10))
            .background(Color.LightGray.copy(alpha = 0.2f))
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(20))
                .border(
                    BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                    RoundedCornerShape(20)
                )
                .background(Color.LightGray.copy(alpha = 0.3f)),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {

            var isTakeout by remember {
                mutableStateOf(true)
            }

            isTakeout = when (city.deliveryType) {
                DeliveryType.TAKE_OUT -> true
                DeliveryType.DELIVERY_TO -> false
            }

            val lightGray30 = Color.LightGray.copy(alpha = 0.3f)

            val deliveryColor by animateColorAsState(
                if (isTakeout) lightGray30 else Color.White, label = "deliveryColor"
            )
            val takeoutColor by animateColorAsState(
                if (isTakeout) Color.White else lightGray30, label = "takeoutColor"
            )

            when (city.deliveryType) {
                DeliveryType.TAKE_OUT -> {
                    TextButton(
                        text = "Доставка",
                        color = deliveryColor
                    ) { onDeliveryClick(DeliveryType.DELIVERY_TO) }
                    TextButton(
                        text = "В пиццерии",
                        color = takeoutColor
                    ) { onDeliveryClick(DeliveryType.TAKE_OUT) }
                }

                DeliveryType.DELIVERY_TO -> {
                    TextButton(
                        text = "Доставка",
                        color = deliveryColor
                    ) { onDeliveryClick(DeliveryType.DELIVERY_TO) }
                    TextButton(
                        text = "В пиццерии",
                        color = takeoutColor
                    ) { onDeliveryClick(DeliveryType.TAKE_OUT) }
                }
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .clickable {
                    onAddressClick()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Выбрать адрес доставки",
                color = colorResource(R.color.orange),
                fontSize = 14.sp
            )
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_angle_right),
                contentDescription = null,
                tint = colorResource(R.color.orange)
            )
        }
    }

}

@Composable
fun TextButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    val displayMetrics: DisplayMetrics = LocalContext.current.resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(20))
            .background(color)
            .width((dpWidth / 2 - 32).dp)
            .height(32.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable {
                onClick()
            },
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        text = text
    )
}

@Composable
fun ChoseCity(
    cityName: String,
    paddingStart: Dp,
    paddingTop: Dp,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = paddingStart, top = paddingTop)
            .clip(RoundedCornerShape(30))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cityName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Icon(
            modifier = Modifier
                .size(28.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_angle_down),
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)
        )
    }
}