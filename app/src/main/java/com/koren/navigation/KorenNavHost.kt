package com.koren.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.koren.auth.navigation.AuthDestination
import com.koren.auth.navigation.authScreen
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.UserSession
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home_screen.HomeDestination
import com.koren.onboarding.navigation.OnboardingGraph
import com.koren.onboarding.navigation.onboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun KorenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    googleAuthService: GoogleAuthService,
    userSession: UserSession
) {
    val coroutineScope = rememberCoroutineScope()

    val startDestination = if (userSession.isLoggedIn) OnboardingGraph else AuthDestination

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        authScreen(onSignInSuccess = { navController.navigate(OnboardingGraph) })
        homeScreen(
            navController = navController,
            logOut = {
                coroutineScope.launch(Dispatchers.IO) {
                    googleAuthService.signOut()
                }
            }
        )
        onboardingScreen(
            navController = navController,
            onNavigateToHome = { navController.navigate(HomeDestination) }
        )
    }
}