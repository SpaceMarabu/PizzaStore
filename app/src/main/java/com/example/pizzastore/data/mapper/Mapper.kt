package com.example.pizzastore.data.mapper

import com.example.pizzastore.data.localdatabase.SessionSettingsDbModel
import com.example.pizzastore.data.network.model.AddressDto
import com.example.pizzastore.data.network.model.PathDto
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.SessionSettings
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun mapSessionSettToDbModel(sessionSettings: SessionSettings) = SessionSettingsDbModel(
        city = sessionSettings.city,
        account = sessionSettings.account
    )

    fun mapSessionSettingsDbModelToEntity(settingsDbModel: SessionSettingsDbModel?): SessionSettings? {
        return if (settingsDbModel == null) {
            null
        } else {
            SessionSettings(
                city = settingsDbModel.city,
                account = settingsDbModel.account
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
