package com.koren.account.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object AccountGraph

fun NavGraphBuilder.accountScreen(
    navController: NavHostController,
    onLogOut: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<AccountGraph>(
        startDestination = AccountDestination
    ) {
        composable<AccountDestination> {
            AccountScreen(
                onLogOut = onLogOut,
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}