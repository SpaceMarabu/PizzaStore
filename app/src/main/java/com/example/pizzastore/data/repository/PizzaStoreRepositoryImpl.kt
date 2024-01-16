package com.example.pizzastore.data.repository

import android.util.Log
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.delay
import javax.inject.Inject

class PizzaStoreRepositoryImpl @Inject constructor() : PizzaStoreRepository {

    private val listCities = mutableListOf<City>()

    init {
        loadCities()
    }

    private fun loadCities() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dRef = database.getReference("cities")


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val key: String = data.key ?: continue
                    val value = data.getValue<String>() ?: continue
                    listCities.add(City(key.toInt(), value))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TEST_TEST", "loadPost:onCancelled", databaseError.toException())
            }
        }

        dRef.addValueEventListener(postListener)
    }


    override fun getCitiesUseCase(): List<City> {
        return listCities
    }
}