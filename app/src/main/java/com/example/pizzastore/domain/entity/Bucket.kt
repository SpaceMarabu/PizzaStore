package com.example.pizzastore.domain.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bucket(
    val order: Map<Product, Int> = mapOf()
): Parcelable {
    constructor(parcel: Parcel) : this(
        mutableMapOf<Product, Int>().apply {
            val size = parcel.readInt()
            repeat(size) {
                val key = parcel.readParcelable<Product>(Product::class.java.classLoader)
                val value = parcel.readInt()
                if (key != null) {
                    this[key] = value
                }
            }
        }
    )

    companion object : Parceler<Bucket> {

        override fun Bucket.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(order.size)
            for ((key, value) in order) {
                parcel.writeParcelable(key, flags)
                parcel.writeInt(value)
            }
        }

        override fun create(parcel: Parcel): Bucket {
            return Bucket(parcel)
        }
    }
}
