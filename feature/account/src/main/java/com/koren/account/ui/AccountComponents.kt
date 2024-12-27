package com.koren.account.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object AccountDestination : Destination

@Composable
fun AccountScreen() {
    AccountScreenContent()
}

@Composable
private fun AccountScreenContent() {
    Text("Account Screen")
}

@ThemePreview
@Composable
fun AccountScreenPreview() {
    KorenTheme {
        AccountScreenContent()
    }
}