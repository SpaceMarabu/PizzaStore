package com.example.pizzastore.data.localdatabase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.City
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "session_settings")
data class SessionSettingsDbModel(
    @PrimaryKey
    var id: Int = 1,
    var city: City? = null,
    var account: Account? = null
) : Parcelable

