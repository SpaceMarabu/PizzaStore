package com.example.pizzastore.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ProductType(
    val type: String = TYPE_PIZZA,
    val frontName: String = FRONT_NAME_PIZZA
) : Parcelable {

    data object PIZZA : ProductType(TYPE_PIZZA, FRONT_NAME_PIZZA)
    data object ROLL : ProductType(TYPE_ROLL, FRONT_NAME_ROLL)
    data object STARTER : ProductType(TYPE_STARTER, FRONT_NAME_STARTER)
    data object DESSERT : ProductType(TYPE_DESSERT, FRONT_NAME_DESSERT)
    data object DRINK : ProductType(TYPE_DRINK, FRONT_NAME_DRINK)

    companion object {
        private const val TYPE_PIZZA = "pizza"
        private const val TYPE_ROLL = "roll"
        private const val TYPE_STARTER = "starter"
        private const val TYPE_DESSERT = "dessert"
        private const val TYPE_DRINK = "drink"

        private const val FRONT_NAME_PIZZA = "Пицца"
        private const val FRONT_NAME_ROLL = "Роллы"
        private const val FRONT_NAME_STARTER = "Стартеры"
        private const val FRONT_NAME_DESSERT = "Десерты"
        private const val FRONT_NAME_DRINK = "Напитки"

        val allTypes = listOf(
            PIZZA,
            ROLL,
            STARTER,
            DESSERT,
            DRINK
        )

        fun fromString(type: String?): ProductType {
            return allTypes.find { it.type == type } ?: PIZZA
        }
    }
}