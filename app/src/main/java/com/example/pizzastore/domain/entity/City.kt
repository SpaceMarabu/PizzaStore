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
    val points: List<String>? = null
) : Parcelable {

    companion object {

        val NavigationType: NavType<String> = object : NavType<String>(true) {

            override fun get(bundle: Bundle, key: String): String? {
                return bundle.getString(key)
            }

            override fun parseValue(value: String): String {
                return value
            }

            override fun put(bundle: Bundle, key: String, value: String) {
                bundle.putString(key, value)
            }
        }
    }

}
