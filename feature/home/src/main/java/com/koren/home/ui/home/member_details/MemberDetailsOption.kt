package com.koren.home.ui.home.member_details

import androidx.compose.ui.graphics.vector.ImageVector

data class MemberDetailsOption(
    val icon: ImageVector,
    val title: String,
    val event: MemberDetailsUiEvent,
    val isEnabled: Boolean = true,
    val description: String = ""
)