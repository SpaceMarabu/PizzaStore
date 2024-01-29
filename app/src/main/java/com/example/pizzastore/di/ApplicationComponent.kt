package com.example.pizzastore.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory

    fun inject(application: PizzaStoreApplication)


    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
//            @BindsInstance context: Context
        ): ApplicationComponent
    }
}
