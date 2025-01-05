package com.koren.account.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import kotlinx.serialization.Serializable

@Serializable
object AccountGraph : Destination

fun NavGraphBuilder.accountScreen(
    navController: NavHostController,
    onLogOut: () -> Unit
) {
    navigation<AccountGraph>(
        startDestination = AccountDestination
    ) {
        composable<AccountDestination> {
            AccountScreen(onLogOut = onLogOut)
        }
    }
}