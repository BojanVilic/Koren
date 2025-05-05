package com.koren.chat.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AttachmentOptions(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
