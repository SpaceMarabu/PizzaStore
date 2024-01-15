package com.example.pizzastore.di

import com.example.pizzastore.data.repository.PizzaStoreRepositoryImpl
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: PizzaStoreRepositoryImpl): PizzaStoreRepository

}
