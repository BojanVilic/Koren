package com.koren.activity.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object ActivityDestination : Destination

@Composable
fun ActivityScreen() {
    ActivityScreenContent()
}

@Composable
private fun ActivityScreenContent() {
    Text("Activity Screen")
}

@ThemePreview
@Composable
fun ActivityScreenPreview() {
    KorenTheme {
        ActivityScreenContent()
    }
}