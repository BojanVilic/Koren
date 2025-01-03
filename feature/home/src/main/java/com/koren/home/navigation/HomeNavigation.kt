package com.koren.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import com.koren.home.ui.HomeDestination
import com.koren.home.ui.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeGraph : Destination

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    inviteFamilyMember: () -> Unit
) {
    navigation<HomeGraph>(
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                inviteFamilyMember = inviteFamilyMember
            )
        }
    }
}