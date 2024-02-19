package com.example.pizzastore.data.mapper

import com.example.pizzastore.data.database.CityDbModel
import com.example.pizzastore.data.network.model.AddressDto
import com.example.pizzastore.data.network.model.PathDto
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Path
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun mapCityEntityToDbModel(city: City) = CityDbModel(
        name = city.name,
        deliveryType = city.deliveryType,
        points = city.points
    )

    fun mapCityDbModelToEntity(cityDbModel: CityDbModel?): City? {
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

    fun mapAddressDtoToEntity(dto: AddressDto): Address {
        return Address(
            city = dto.city,
            street = dto.street,
            houseNumber = dto.houseNumber
        )
    }

    fun mapPathDtoToEntity(dto: PathDto): Path {
        return Path(
            distance = dto.distance,
            time = dto.time
        )
    }

}
