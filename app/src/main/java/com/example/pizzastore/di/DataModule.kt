package com.example.pizzastore.di

import android.app.Application
import com.example.pizzastore.data.database.CityDao
import com.example.pizzastore.data.database.LocalDatabase
import com.example.pizzastore.data.repository.PizzaStoreRepositoryImpl
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: PizzaStoreRepositoryImpl): PizzaStoreRepository



    companion object {

//        @ApplicationScope
//        @Provides
//        fun provideFirebaseDatabase(): FirebaseDatabase {
//            return FirebaseDatabase.getInstance()
//        }

        @ApplicationScope
        @Provides
        fun provideCityDao(
            application: Application
        ): CityDao {
            return LocalDatabase.getInstance(application).cityDao()
        }
    }

}
