package com.example.pizzastore.presentation.funs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClickableIconByResourceId(
    size: Dp = 25.dp,
    resourceId: Int,
    color: Color = Color.Black,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .size(size)
            .padding(
                start = 4.dp,
                end = 4.dp
            )
            .clickable {
                onClick()
            },
        imageVector = ImageVector.vectorResource(resourceId),
        contentDescription = "clickable_icon",
        tint = color
    )
}