package com.example.pizzastore.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.getApplicationComponent
import com.example.pizzastore.ui.theme.PizzaStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())

            val cityState = viewModel.state.collectAsState()

            PizzaStoreTheme {

                when (cityState.value) {

                    is CityScreenState.CityContent -> {
                        MenuScreen((cityState.value as CityScreenState.CityContent).city)
                    }

                    CityScreenState.Initial -> {
                        ChoseCityScreen {
                            viewModel.changeState(CityScreenState.CityContent(it))
                        }
                    }
                }
            }

        }
    }
}
