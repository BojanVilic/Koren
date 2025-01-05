package com.koren.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.koren.MainActivityUiState
import com.koren.MainActivityViewModel
import com.koren.account.ui.accountScreen
import com.koren.activity.ui.activityScreen
import com.koren.auth.navigation.AuthDestination
import com.koren.auth.navigation.authScreen
import com.koren.auth.service.GoogleAuthService
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.HomeDestination
import com.koren.invitation.navigation.invitationScreen
import com.koren.invitation.ui.InvitationDestination
import com.koren.map.ui.mapScreen
import com.koren.onboarding.navigation.OnboardingGraph
import com.koren.onboarding.navigation.onboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun KorenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
) {
    val uiState = mainActivityViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination = when {
        uiState.value is MainActivityUiState.LoggedOut -> AuthDestination
        uiState.value is MainActivityUiState.Success && (uiState.value as MainActivityUiState.Success).userData.familyId.isNotEmpty() -> HomeGraph
        else -> OnboardingGraph
    }

    if (uiState.value !is MainActivityUiState.Loading) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            authScreen(
                onSignInSuccess = {
                    mainActivityViewModel.onLoginSuccess()
                    navController.navigate(HomeGraph)
                },
            )
            homeScreen(
                navController = navController,
                inviteFamilyMember = {
                    navController.navigate(InvitationDestination)
                }
            )
            onboardingScreen(
                navController = navController,
                onNavigateToHome = { navController.navigate(HomeDestination) }
            )
            mapScreen(navController = navController)
            activityScreen(navController = navController)
            accountScreen(
                navController = navController,
                onLogOut = { navController.navigate(AuthDestination) }
            )
            invitationScreen(navController = navController)
        }
    }
}