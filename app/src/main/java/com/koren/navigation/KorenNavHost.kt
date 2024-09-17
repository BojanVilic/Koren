package com.koren.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.koren.auth.navigation.AuthScreenDestination
import com.koren.auth.navigation.authScreen
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.UserSession
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home_screen.HomeScreenDestination
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

    val startDestination = if (userSession.isLoggedIn) HomeGraph else AuthScreenDestination

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        authScreen(
            onSignInSuccess = {
                navController.navigate(HomeScreenDestination)
            }
        )
        homeScreen(
            navController = navController,
            logOut = {
                coroutineScope.launch(Dispatchers.IO) {
                    googleAuthService.signOut()
                }
            }
        )
    }
}