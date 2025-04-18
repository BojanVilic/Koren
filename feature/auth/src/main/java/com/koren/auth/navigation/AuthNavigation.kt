package com.koren.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.auth.ui.sign_in.SignInScreen
import com.koren.auth.ui.sign_in.SignInDestination
import com.koren.auth.ui.sign_up.SignUpScreen
import com.koren.auth.ui.sign_up.SignUpDestination
import kotlinx.serialization.Serializable

@Serializable
object AuthGraph

fun NavGraphBuilder.authScreen(
    navController: NavController,
    onSignInSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<AuthGraph>(
        startDestination = SignInDestination
    ) {
        composable<SignInDestination> {
            SignInScreen(
                onSignInSuccess = onSignInSuccess,
                onNavigateToOnboarding = onSignUpSuccess,
                navigateToSignUp = { navController.navigate(SignUpDestination) },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<SignUpDestination> {
            SignUpScreen(
                onSignUpSuccess = onSignUpSuccess,
                onNavigateBack = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}