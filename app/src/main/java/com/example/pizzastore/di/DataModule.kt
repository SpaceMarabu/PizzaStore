package com.example.pizzastore.di

import android.app.Application
import com.example.pizzastore.data.localdatabase.CityDao
import com.example.pizzastore.data.localdatabase.LocalDatabase
import com.example.pizzastore.data.remotedatabase.DatabaseService
import com.example.pizzastore.data.remotedatabase.FirebaseImpl
import com.example.pizzastore.data.repository.PizzaStoreRepositoryImpl
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: PizzaStoreRepositoryImpl): PizzaStoreRepository



    companion object {



            @ApplicationScope
            @Provides
            fun provideFirebase() : DatabaseService {
                return  FirebaseImpl()
            }


        @ApplicationScope
        @Provides
        fun provideCityDao(
            application: Application
        ): CityDao {
            return LocalDatabase.getInstance(application).cityDao()
        }
    }

}
