package com.example.pizzastore.data.repository

import android.util.Log
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PizzaStoreRepositoryImpl @Inject constructor() : PizzaStoreRepository {


    private val citiesFlow = callbackFlow {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dRef = database.getReference("cities")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listCities = mutableListOf<City>()
                for (data in dataSnapshot.children) {
                    val key: String = data.key ?: continue
                    val value = data.getValue(City::class.java) ?: continue
                    listCities.add(
                        City(
                            id = key.toInt(),
                            name = value.name,
                            points = value.points
                        )
                    )
                }
                val returnList = listCities.toList()
                trySend(returnList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TEST_TEST", "loadPost:onCancelled", databaseError.toException())
            }
        }
        dRef.addValueEventListener(postListener)

        awaitClose {
            dRef.removeEventListener(postListener)
        }
    }


    override fun getCitiesUseCase(): Flow<List<City>> {
        return citiesFlow
    }
}