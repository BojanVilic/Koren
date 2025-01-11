package com.koren.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.auth.ui.sign_in.SignInScreen
import com.koren.auth.ui.sign_up.SignUpScreen
import com.koren.common.util.Destination
import kotlinx.serialization.Serializable

@Serializable
object AuthGraph : Destination

fun NavGraphBuilder.authScreen(
    navController: NavController,
    onSignInSuccess: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<AuthGraph>(
        startDestination = SignInScreen
    ) {
        composable<SignInScreen> {
            SignInScreen(
                onSignInSuccess = onSignInSuccess,
                navigateToSignUp = { navController.navigate(SignUpScreen) },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<SignUpScreen> {
            SignUpScreen(
                onSignUpSuccess = onSignInSuccess,
                onNavigateBack = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}