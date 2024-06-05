package com.example.pizzastore.data.localdatabase

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pizzastore.data.localdatabase.entity.SessionSettingsDbModel

@Database(entities = [SessionSettingsDbModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun cityDao(): PizzaDao

    companion object {

        private var INSTANCE: LocalDatabase? = null
        private var LOCK = Any()
        private const val DB_NAME = "pizza_store.db"

        fun getInstance(application: Application): LocalDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    LocalDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = db
                return db
            }
        }
    }
}