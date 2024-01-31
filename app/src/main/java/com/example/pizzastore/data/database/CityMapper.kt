package com.example.pizzastore.data.database

import com.example.pizzastore.domain.entity.City
import javax.inject.Inject

class CityMapper @Inject constructor() {

    fun mapEntityToDbModel(city: City) = CityDbModel(
        name = city.name,
        deliveryType = city.deliveryType,
        points = city.points
    )

    fun mapDbModelToEntity(cityDbModel: CityDbModel?): City? {
        return if (cityDbModel == null) {
            null
        } else {
            City(
                id = cityDbModel.id,
                name = cityDbModel.name,
                deliveryType = cityDbModel.deliveryType,
                points = cityDbModel.points
            )
        }
    }
}
