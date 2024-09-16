package com.koren.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import com.koren.home.ui.create_family.CreateFamilyScreen
import com.koren.home.ui.create_family.CreateFamilyScreenDestination
import com.koren.home.ui.home_screen.HomeScreen
import com.koren.home.ui.home_screen.HomeScreenDestination
import kotlinx.serialization.Serializable

@Serializable
object HomeGraph : Destination

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    logOut: () -> Unit,
) {
    navigation<HomeGraph>(
        startDestination = HomeScreenDestination
    ) {
        composable<HomeScreenDestination> {
            HomeScreen(
                logOut = logOut,
                createFamily = { navController.navigate(CreateFamilyScreenDestination) }
            )
        }
        composable<CreateFamilyScreenDestination> {
            CreateFamilyScreen()
        }
    }
}