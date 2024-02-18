package com.example.pizzastore.data.repository

import android.util.Log
import com.example.cryptoapp.data.network.ApiFactory
import com.example.pizzastore.data.database.CityDao
import com.example.pizzastore.data.database.Mapper
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import com.example.pizzastore.presentation.funs.mergeWith
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class PizzaStoreRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val mapper: Mapper
) : PizzaStoreRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private var currentCityFlow = MutableSharedFlow<City>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val listCitiesFlow = callbackFlow {
        val dRef = firebaseDatabase.getReference("cities")

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


    override suspend fun getAddressUseCase(pointLatLng: String): Address {
        val addressDto = ApiFactory.apiService.getAddress(pointLatLng)
        addressDto
        var result = mapper.mapAddressDtoToEntity(
            addressDto.addressList[0]
        )
        addressDto.addressList.forEach {
            if (it.houseNumber != null) {
                result = mapper.mapAddressDtoToEntity(it)
                return@forEach
            }
        }
        return result
    }

    override suspend fun setCitySettingsUseCase(city: City) {
        currentCityFlow.emit(city)
        val dbModel = mapper.mapCityEntityToDbModel(city)
        cityDao.addCity(dbModel)
    }

    override fun getCurrentCityUseCase(): Flow<City?> {
        return cityDao.get().map {
            mapper.mapCityDbModelToEntity(it)
        }.mergeWith(currentCityFlow)
    }

    override fun getCitiesUseCase(): Flow<List<City>> {
        return listCitiesFlow
    }
}