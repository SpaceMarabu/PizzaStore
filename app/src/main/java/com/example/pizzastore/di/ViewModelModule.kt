package com.example.pizzastore.di

import androidx.lifecycle.ViewModel
import com.example.pizzastore.presentation.chosecity.CityDeliveryViewModel
import com.example.pizzastore.presentation.start.StartScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(StartScreenViewModel::class)
    @Binds
    fun bindMainViewModel(viewModel: StartScreenViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CityDeliveryViewModel::class)
    @Binds
    fun bindCityDeliveryViewModel(viewModel: CityDeliveryViewModel): ViewModel
}
