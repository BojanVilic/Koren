package com.koren.activity.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import kotlinx.serialization.Serializable

@Serializable
object ActivityGraph : Destination

fun NavGraphBuilder.activityScreen(
    navController: NavHostController
) {
    navigation<ActivityGraph>(
        startDestination = ActivityDestination
    ) {
        composable<ActivityDestination> {
            ActivityScreen()
        }
    }
}