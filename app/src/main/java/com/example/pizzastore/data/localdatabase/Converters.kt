package com.example.pizzastore.data.localdatabase

import androidx.room.TypeConverter
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.OrdersHistory
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
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

    @TypeConverter
    fun productToEntity(product: Product?): String? {
        return product?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun entityToProduct(product: String?): Product? {
        return product?.let {
            val type: Type = object : TypeToken<Product?>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun ordersHistoryToEntity(ordersHistory: OrdersHistory?): String? {
        return ordersHistory?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun entityToOrdersHistory(ordersHistory: String?): OrdersHistory? {
        return ordersHistory?.let {
            val type: Type = object : TypeToken<OrdersHistory?>() {}.type
            Gson().fromJson(it, type)
        }
    }


    @TypeConverter
    fun fromProductsList(products: List<Product?>?): String? {
        if (products == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Product?>?>() {}.type
        return gson.toJson(products, type)
    }

    @TypeConverter
    fun toProductsList(products: String?): List<Product?>? {
        if (products == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Product?>?>() {}.type
        return gson.fromJson(products, type)
    }
    @TypeConverter
    fun fromOrderList(orders: List<Order?>?): String? {
        if (orders == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Order?>?>() {}.type
        return gson.toJson(orders, type)
    }

    @TypeConverter
    fun toOrderList(orders: String?): List<Order?>? {
        if (orders == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<List<Order?>?>() {}.type
        return gson.fromJson(orders, type)
    }

    @TypeConverter
    fun bucketToEntity(bucket: Bucket?): String? {
        if (bucket == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Bucket?>() {}.type
        return gson.toJson(bucket, type)
    }

    @TypeConverter
    fun entityToBucket(bucket: String?): Bucket? {
        if (bucket == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Bucket?>() {}.type
        return gson.fromJson(bucket, type)
    }

    @TypeConverter
    fun bucketMapToEntity(bucketMap: Map<Product, Int>?): String? {
        if (bucketMap == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Map<Product, Int>?>() {}.type
        return gson.toJson(bucketMap, type)
    }

    @TypeConverter
    fun entityToBucketMap(bucketMap: String?): Map<Product, Int>? {
        if (bucketMap == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object :
            TypeToken<Map<Product, Int>?>() {}.type
        return gson.fromJson(bucketMap, type)
    }


}