package com.example.pizzastore.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Point
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "session_settings")
data class CityDbModel(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Москва",
    val deliveryType: DeliveryType = DeliveryType.TAKE_OUT,
    val points: List<Point> = listOf()
) : Parcelable { }
