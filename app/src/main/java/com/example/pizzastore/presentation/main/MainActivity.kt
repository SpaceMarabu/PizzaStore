package com.example.pizzastore.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.presentation.city.ChoseCityScreen
import com.example.pizzastore.presentation.menu.MainScreen
import com.example.pizzastore.ui.theme.PizzaStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            val component = getApplicationComponent()
            val viewModel: StartScreenViewModel = viewModel(factory = component.getViewModelFactory())

            val cityState = viewModel.state.collectAsState()

            PizzaStoreTheme {

                when (cityState.value) {

                    is StartScreenState.StartScreenContent -> {
                        MainScreen((cityState.value as StartScreenState.StartScreenContent).city)
                    }

                    StartScreenState.Initial -> {
                        ChoseCityScreen {
                            viewModel.changeState(StartScreenState.StartScreenContent(it))
                        }
                    }
                }
            }

        }
    }
}
