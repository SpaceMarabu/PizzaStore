package com.example.pizzastore

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.pizzastore.di.ApplicationComponent
import com.example.pizzastore.di.DaggerApplicationComponent

class PizzaStoreApplication : Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}

@Composable
fun getApplicationComponent(): ApplicationComponent {
    return (LocalContext.current.applicationContext as PizzaStoreApplication).component
}
