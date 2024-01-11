package com.example.pizzastore.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzastore.domain.City

@Composable
fun ChoseCityScreen(
    onCityChosen: (city: City) -> Unit
) {

    val listCities = listOf(
        City(1, "Москва", ),
        City(2, "Казань"),
        City(3, "Долгопрудный")
    )

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