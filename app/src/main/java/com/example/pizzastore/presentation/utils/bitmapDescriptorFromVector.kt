package com.example.pizzastore.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {

    val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable?.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}