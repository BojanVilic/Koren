package com.koren.account.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.account.ui.account.AccountDestination
import com.koren.account.ui.account.AccountScreen
import com.koren.account.ui.edit_profile.EditProfileDestination
import com.koren.account.ui.edit_profile.EditProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object AccountGraph

fun NavGraphBuilder.accountScreen(
    navController: NavHostController,
    onLogOut: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    navigateToActivity: () -> Unit
) {
    navigation<AccountGraph>(
        startDestination = AccountDestination
    ) {
        composable<AccountDestination> {
            AccountScreen(
                onLogOut = onLogOut,
                onShowSnackbar = onShowSnackbar,
                navigateToEditProfile = { navController.navigate(EditProfileDestination) },
                navigateToActivity = navigateToActivity
            )
        }
        composable<EditProfileDestination> {
            EditProfileScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}