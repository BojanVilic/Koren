package com.koren.onboarding.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object OnboardingDestination : Destination

@Composable
fun OnboardingScreen(
    joinFamily: () -> Unit,
    createFamily: () -> Unit
) {
    OnboardingContent(
        joinFamily = joinFamily,
        createFamily = createFamily
    )
}

@Composable
private fun OnboardingContent(
    joinFamily: () -> Unit,
    createFamily: () -> Unit
) {
    Column {
        Button(
            onClick = createFamily
        ) {
            Text(text = "Create Family")
        }

        Button(
            onClick = joinFamily
        ) {
            Text(text = "Join Family")
        }
    }
}

@ThemePreview
@Composable
fun OnboardingPreview() {
    KorenTheme {
        OnboardingContent(
            joinFamily = {},
            createFamily = {}
        )
    }
}