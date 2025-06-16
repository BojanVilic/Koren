package com.koren.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val event: Color,
    val task: Color,
    val currentUserBorderMap: Color
)

val LightExtendedColors = ExtendedColors(
    event = Color(0xFF014CD7),
    task = Color(0xFF4FAD05),
    currentUserBorderMap = Color(0xFFC58822)
)

val DarkExtendedColors = ExtendedColors(
    event = Color(0xFF67AAF3),
    task = Color(0xFF97D27C),
    currentUserBorderMap = Color(0xFFFFA61F)
)

object ExtendedTheme {
    val colorScheme: ExtendedColors
        @Composable
        get() = LocalCustomColors.current
}

val LocalCustomColors = compositionLocalOf<ExtendedColors> {
    error("No ExtendedColors provided")
}