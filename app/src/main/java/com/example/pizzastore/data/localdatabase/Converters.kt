package com.example.pizzastore.data.localdatabase

import androidx.room.TypeConverter
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Point
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object Converters {

    @TypeConverter
    fun fromPointsList(points: List<Point?>?): String? {
        if (points == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Point?>?>() {}.type
        return gson.toJson(points, type)
    }

    @TypeConverter
    fun toPointsList(points: String?): List<Point?>? {
        if (points == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Point?>?>() {}.type
        return gson.fromJson(points, type)
    }

    @TypeConverter
    fun cityToEntity(city: City?): String? {
        if (city == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<City?>() {}.type
        return gson.toJson(city, type)
    }

    @TypeConverter
    fun entityToCity(city: String?): City? {
        if (city == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<City?>() {}.type
        return gson.fromJson(city, type)
    }

    @TypeConverter
    fun accountToEntity(account: Account?): String? {
        if (account == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Account?>() {}.type
        return gson.toJson(account, type)
    }

    @TypeConverter
    fun entityToAccount(account: String?): Account? {
        if (account == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Account?>() {}.type
        return gson.fromJson(account, type)
    }
}