package com.example.pizzastore.presentation.chosecity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.presentation.funs.CircularLoading
import com.example.pizzastore.ui.theme.DarkBlue

@Composable
fun ChoseCityScreen(
    onCityAndDeliveryChosen: () -> Unit
) {

    val component = getApplicationComponent()
    val viewModel: CityDeliveryViewModel = viewModel(factory = component.getViewModelFactory())

    val screenState = viewModel.state.collectAsState()


    when (screenState.value) {

        is CityDeliveryScreenState.Initial -> {}

        is CityDeliveryScreenState.ListCities -> {
            val currentState = screenState.value as CityDeliveryScreenState.ListCities
            val currentCities = currentState.cities
            ChoseCity(
                listCities = currentCities
            ) {
                viewModel.changeState(CityDeliveryScreenState.CityChecked(it))
            }
        }

        is CityDeliveryScreenState.CityChecked -> {
            val currentState = screenState.value as CityDeliveryScreenState.CityChecked
            val currentCity = currentState.city
            ChoseDeliveryType {
                val newCity = currentCity.copy(deliveryType = it)
                viewModel.sendCity(newCity)
                onCityAndDeliveryChosen()
            }
            BackHandler {
                val previousState = viewModel.previousState
                viewModel.changeState(previousState)
            }
        }

        CityDeliveryScreenState.Loading -> {
            CircularLoading()
        }
    }


}

@Composable
fun ChoseCity(
    listCities: List<City>,
    onCityChosen: (city: City) -> Unit
) {
    LazyColumn {
        items(items = listCities, key = { it.id }) { city ->
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onCityChosen(city)
                    },
                text = city.name,
                fontSize = 16.sp
            )
            Divider(
                modifier = Modifier
                    .padding(start = 8.dp),
                color = Color.Gray,
                thickness = 1.dp
            )

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoseDeliveryType(onDeliveryClicked: (DeliveryType) -> Unit) {

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.15f))
        ) {
            Spacer(
                modifier = Modifier
                    .height(64.dp)
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = stringResource(R.string.delivery_question),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(
                modifier = Modifier
                    .height(128.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10))
                    .border(
                        border = BorderStroke(
                            1.dp,
                            Color.LightGray
                        ),
                        shape = RoundedCornerShape(10)
                    )
                    .background(Color.White)
            ) {
                RowDelivery(
                    iconId = R.drawable.ic_delivery,
                    text1 = "Доставка",
                    text2 = "До вашего адреса"
                ) {
                    onDeliveryClicked(DeliveryType.DELIVERY_TO)
                }
                Divider(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
                RowDelivery(
                    iconId = R.drawable.ic_cultery,
                    text1 = "В пиццерии",
                    text2 = "С собой или в зале"
                ) {
                    onDeliveryClicked(DeliveryType.TAKE_OUT)
                }
            }
        }
    }
}

@Composable
fun RowDelivery(iconId: Int, text1: String, text2: String, onDeliveryClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable {
                onDeliveryClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(25.dp),
                imageVector = ImageVector.vectorResource(iconId),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = text1,
                    fontSize = 16.sp
                )
                Text(
                    text = text2,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(25.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_angle_right),
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = 0.2f)
        )
    }
}