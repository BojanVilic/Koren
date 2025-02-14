package com.koren.activity.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object ActivityGraph

fun NavGraphBuilder.activityScreen(
    navController: NavHostController,
    navigateToCalendar: () -> Unit
) {
    navigation<ActivityGraph>(
        startDestination = ActivityDestination
    ) {
        composable<ActivityDestination> {
            ActivityScreen(
                navigateToCalendar = {
                    navigateToCalendar()
                }
            )
        }
    }
}