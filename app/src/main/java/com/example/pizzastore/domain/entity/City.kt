package com.example.pizzastore.domain.entity

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val id: Int = 1,
    val name: String = "Москва",
    val deliveryType: DeliveryType = DeliveryType.TAKE_OUT,
    val points: List<String> = listOf("")
) : Parcelable { }
