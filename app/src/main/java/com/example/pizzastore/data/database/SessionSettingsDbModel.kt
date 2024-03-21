package com.example.pizzastore.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "session_settings")
data class SessionSettingsDbModel(
    @PrimaryKey
    val id: Int = 1,
    val city: City? = null,
    val account: Account? = null
) : Parcelable

//data class CityDbModel(
//    @PrimaryKey
//    val id: Int = 1,
//    val name: String = "Москва",
//    val deliveryType: DeliveryType = DeliveryType.TAKE_OUT,
//    val points: List<Point> = listOf()
//) : Parcelable
