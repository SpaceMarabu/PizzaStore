package com.example.pizzastore.data.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM session_settings LIMIT 1")
    fun get(): Flow<SessionSettingsDbModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSessionSettings(cityDbModel: SessionSettingsDbModel)

}