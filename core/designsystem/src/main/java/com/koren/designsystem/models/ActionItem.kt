package com.koren.designsystem.models

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class ActionItem(
    val icon: IconResource,
    val text: String,
    val onClick: () -> Unit
) {
    @Composable
    fun IconComposable() =
        when (icon) {
            is IconResource.Vector -> Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon.imageVector,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface
            )
            is IconResource.Drawable -> Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon.id),
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
}

sealed interface IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource
    data class Drawable(@DrawableRes val id: Int) : IconResource
}