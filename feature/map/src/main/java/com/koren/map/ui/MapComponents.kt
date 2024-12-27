package com.koren.map.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object MapDestination : Destination

@Composable
fun MapScreen() {
    MapScreenContent()
}

@Composable
private fun MapScreenContent() {
    Text("Map Screen")
}

@ThemePreview
@Composable
fun MapScreenPreview() {
    KorenTheme {
        MapScreenContent()
    }
}