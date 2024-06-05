package com.example.pizzastore.data.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pizzastore.data.localdatabase.entity.SessionSettingsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PizzaDao {

    @Query("SELECT * FROM session_settings LIMIT 1")
    fun get(): Flow<SessionSettingsDbModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSessionSettings(settingsDbModel: SessionSettingsDbModel)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addAccount()

}