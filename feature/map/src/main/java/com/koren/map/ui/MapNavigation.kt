package com.koren.map.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object MapGraph

fun NavGraphBuilder.mapScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<MapGraph>(
        startDestination = MapDestination()
    ) {
        composable<MapDestination> { backStackEntry ->
            val mapDestination = backStackEntry.toRoute<MapDestination>()
            MapScreen(
                onShowSnackbar = onShowSnackbar,
                userId = mapDestination.userId
            )
        }
    }
}