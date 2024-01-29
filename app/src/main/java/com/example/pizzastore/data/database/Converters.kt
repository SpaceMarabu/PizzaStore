package com.example.pizzastore.data.database

import androidx.room.TypeConverter
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
}