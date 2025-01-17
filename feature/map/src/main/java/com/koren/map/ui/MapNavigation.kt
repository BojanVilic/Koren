package com.koren.map.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object MapGraph

fun NavGraphBuilder.mapScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<MapGraph>(
        startDestination = MapDestination
    ) {
        composable<MapDestination> {
            MapScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}