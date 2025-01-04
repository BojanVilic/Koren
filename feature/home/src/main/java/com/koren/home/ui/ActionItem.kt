package com.koren.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.koren.home.R

data class ActionItem(
    val icon: IconResource,
    val text: String,
    val onClick: () -> Unit
) {
    @Composable
    fun IconComposable() =
        when (icon) {
            is IconResource.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = text
            )
            is IconResource.Drawable -> Icon(
                painter = painterResource(id = icon.id),
                contentDescription = text
            )
        }
}

sealed interface IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource
    data class Drawable(@DrawableRes val id: Int) : IconResource
}