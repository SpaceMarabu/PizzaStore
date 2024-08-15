package com.example.pizzastore.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun getOutlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
    cursorColor = MaterialTheme.colorScheme.onSecondary,
    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimary,
).copy(focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary)