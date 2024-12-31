package com.koren.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import com.koren.home.ui.home_screen.HomeDestination
import com.koren.home.ui.home_screen.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeGraph : Destination

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    logOut: () -> Unit,
    inviteFamilyMember: () -> Unit
) {
    navigation<HomeGraph>(
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                logOut = logOut,
                createFamily = {  },
                inviteFamilyMember = inviteFamilyMember
            )
        }
    }
}