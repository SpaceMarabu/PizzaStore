package com.example.pizzastore.data.localdatabase.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "session_settings")
data class SessionSettingsDbModel(
    @PrimaryKey
    var id: Int = 1,
    var city: CityDbModel? = null,
    var account: AccountDbModel? = null
) : Parcelable

