package com.example.pizzastore.presentation.utils

import android.content.Context
import android.widget.Toast

fun showToastWarn(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}