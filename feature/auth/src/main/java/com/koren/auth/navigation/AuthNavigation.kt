package com.koren.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.koren.auth.ui.SignInScreen
import com.koren.common.util.Destination
import kotlinx.serialization.Serializable

@Serializable
object AuthDestination : Destination

fun NavGraphBuilder.authScreen(
    onSignInSuccess: () -> Unit
) {
    composable<AuthDestination> {
        SignInScreen(
            onSignInSuccess = onSignInSuccess
        )
    }
}