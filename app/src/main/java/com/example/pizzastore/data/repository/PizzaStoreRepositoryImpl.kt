package com.example.pizzastore.data.repository

import android.util.Log
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PizzaStoreRepositoryImpl @Inject constructor() : PizzaStoreRepository {


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val currentCityFlow: MutableStateFlow<City?> = MutableStateFlow(null)


    private val listCitiesFlow = callbackFlow {
        val dRef = database.getReference("cities")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listCities = mutableListOf<City>()
                for (data in dataSnapshot.children) {
                    val key: String = data.key ?: continue
                    val value = data.getValue(City::class.java) ?: continue
                    value.points
                    listCities.add(
                        City(
                            id = key.toInt(),
                            name = value.name,
                            points = value.points.filterNotNull()
                        )
                    )
                }
                val returnList = listCities.toList()
                trySend(returnList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "PizzaStoreRepositoryImpl",
                    "loadCities:onCancelled",
                    databaseError.toException()
                )
            }
        }
        dRef.addValueEventListener(postListener)

        awaitClose {
            dRef.removeEventListener(postListener)
        }
    }

    override suspend fun setCitySettings(city: City) {
        currentCityFlow.emit(city)
    }

    override fun getCurrentCityUseCase(): Flow<City?> {
        return currentCityFlow
    }

    override fun getCitiesUseCase(): Flow<List<City>> {
        return listCitiesFlow
    }
}