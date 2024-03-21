package com.example.pizzastore.di

import androidx.lifecycle.ViewModel
import com.example.pizzastore.presentation.chosecity.CityDeliveryViewModel
import com.example.pizzastore.presentation.home.MainViewModel
import com.example.pizzastore.presentation.mapscreen.delivery.DeliveryMapScreenViewModel
import com.example.pizzastore.presentation.mapscreen.takeout.TakeoutMapScreenViewModel
import com.example.pizzastore.presentation.menu.MenuScreenViewModel
import com.example.pizzastore.presentation.start.StartScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(StartScreenViewModel::class)
    @Binds
    fun bindStartViewModel(viewModel: StartScreenViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel


    @IntoMap
    @ViewModelKey(CityDeliveryViewModel::class)
    @Binds
    fun bindCityDeliveryViewModel(viewModel: CityDeliveryViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MenuScreenViewModel::class)
    @Binds
    fun bindMenuScreenViewModel(viewModel: MenuScreenViewModel): ViewModel

    @IntoMap
    @ViewModelKey(TakeoutMapScreenViewModel::class)
    @Binds
    fun bindTOMapScreenViewModel(viewModel: TakeoutMapScreenViewModel): ViewModel

    @IntoMap
    @ViewModelKey(DeliveryMapScreenViewModel::class)
    @Binds
    fun bindDMapScreenViewModel(viewModel: DeliveryMapScreenViewModel): ViewModel
}
