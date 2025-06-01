package com.koren.designsystem.models

import androidx.compose.ui.graphics.vector.ImageVector

data class ActionItem(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)