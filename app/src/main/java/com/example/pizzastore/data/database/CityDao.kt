package com.example.pizzastore.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM session_settings LIMIT 1")
    fun get(): Flow<CityDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCity(cityDbModel: CityDbModel)

}