package com.koren.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.koren.MainActivityUiState
import com.koren.MainActivityViewModel
import com.koren.account.ui.accountScreen
import com.koren.activity.ui.activityScreen
import com.koren.auth.navigation.AuthGraph
import com.koren.auth.navigation.authScreen
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home.HomeDestination
import com.koren.invitation.navigation.invitationScreen
import com.koren.invitation.ui.InvitationDestination
import com.koren.map.ui.mapScreen
import com.koren.onboarding.navigation.OnboardingGraph
import com.koren.onboarding.navigation.onboardingScreen

@Composable
fun KorenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    val uiState = mainActivityViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination: Any = when {
        uiState.value is MainActivityUiState.LoggedOut -> AuthGraph
        else -> HomeGraph
    }

    if (uiState.value !is MainActivityUiState.Loading) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            authScreen(
                navController = navController,
                onSignInSuccess = {
                    mainActivityViewModel.onSignInSuccess()
                    navController.navigate(HomeGraph)
                },
                onShowSnackbar = onShowSnackbar
            )
            homeScreen(
                navController = navController,
                inviteFamilyMember = {
                    navController.navigate(InvitationDestination)
                },
                createFamily = { navController.navigate(OnboardingGraph) },
                onShowSnackbar = onShowSnackbar
            )
            onboardingScreen(
                navController = navController,
                onNavigateToHome = { navController.navigate(HomeDestination) }
            )
            mapScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar
            )
            activityScreen(navController = navController)
            accountScreen(
                navController = navController,
                onLogOut = {
                    navController.navigate(AuthGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
            invitationScreen(navController = navController)
        }
    }
}