package com.koren.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.koren.MainActivityUiState
import com.koren.MainActivityViewModel
import com.koren.account.ui.account.AccountDestination
import com.koren.account.ui.navigation.accountScreen
import com.koren.activity.ui.ActivityDestination
import com.koren.activity.ui.activityScreen
import com.koren.auth.navigation.AuthGraph
import com.koren.auth.navigation.authScreen
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home.HomeDestination
import com.koren.invitation.navigation.invitationScreen
import com.koren.invitation.ui.InvitationDestination
import com.koren.map.ui.MapDestination
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
                onShowSnackbar = onShowSnackbar,
                navigateToActivity = {
                    navController.navigateToTopLevelDestination(ActivityTopLevelRoute)
                }
            )
            invitationScreen(navController = navController)
        }
    }
}

fun NavHostController.navigateToTopLevelDestination(topLevelRoute: TopLevelRoute) {
    val navController = this
    val topLevelNavOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    when (topLevelRoute) {
        is HomeTopLevelRoute -> navController.navigate(HomeDestination, topLevelNavOptions)
        is MapTopLevelRoute -> navController.navigate(MapDestination, topLevelNavOptions)
        is ActivityTopLevelRoute -> navController.navigate(ActivityDestination, topLevelNavOptions)
        is AccountTopLevelRoute -> navController.navigate(AccountDestination, topLevelNavOptions)
    }
}