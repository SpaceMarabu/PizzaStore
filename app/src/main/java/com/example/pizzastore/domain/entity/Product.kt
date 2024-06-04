package com.example.pizzastore.domain.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.pizzastore.data.localdatabase.Converters
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
@TypeConverters(Converters::class)
data class Product(
    val id: Int = -1,
    val type: ProductType = ProductType.PIZZA,
    val name: String = "",
    val price: Int = 0,
    var photo: String? = null,
    val description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
            " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        type = ProductType.fromString(parcel.readString()),
        name = parcel.readString() ?: "",
        price = parcel.readInt(),
        photo = parcel.readString(),
        description = parcel.readString() ?: ""
    )

    companion object : Parceler<Product> {

        override fun Product.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(type.type)
            parcel.writeString(name)
            parcel.writeInt(price)
            parcel.writeString(photo)
            parcel.writeString(description)
        }

        override fun create(parcel: Parcel): Product {
            return Product(parcel)
        }
    }
}
