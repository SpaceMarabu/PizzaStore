package com.example.pizzastore.data.localdatabase

import androidx.room.TypeConverter
import com.example.pizzastore.data.localdatabase.entity.AccountDbModel
import com.example.pizzastore.data.localdatabase.entity.CityDbModel
import com.example.pizzastore.data.localdatabase.entity.PointDbModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object Converters {

    @TypeConverter
    fun fromPointsList(points: List<PointDbModel?>?): String? {
        if (points == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<PointDbModel?>?>() {}.type
        return gson.toJson(points, type)
    }

    @TypeConverter
    fun toPointsList(points: String?): List<PointDbModel?>? {
        if (points == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<PointDbModel?>?>() {}.type
        return gson.fromJson(points, type)
    }

    @TypeConverter
    fun cityToEntity(city: CityDbModel?): String? {
        if (city == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<CityDbModel?>() {}.type
        return gson.toJson(city, type)
    }

    @TypeConverter
    fun entityToCity(city: String?): CityDbModel? {
        if (city == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<CityDbModel?>() {}.type
        return gson.fromJson(city, type)
    }

    @TypeConverter
    fun accountToEntity(accountDbModel: AccountDbModel?): String? {
        if (accountDbModel == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<AccountDbModel?>() {}.type
        return gson.toJson(accountDbModel, type)
    }

    @TypeConverter
    fun entityToAccount(account: String?): AccountDbModel? {
        if (account == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<AccountDbModel?>() {}.type
        return gson.fromJson(account, type)
    }

}