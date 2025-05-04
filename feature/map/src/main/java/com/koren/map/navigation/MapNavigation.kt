package com.koren.map.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.koren.map.ui.MapDestination
import com.koren.map.ui.MapScreen
import com.koren.map.ui.edit_places.EditPlacesDestination
import com.koren.map.ui.edit_places.EditPlacesScreen
import com.koren.map.ui.save_location.SaveLocationDestination
import com.koren.map.ui.save_location.SaveLocationScreen
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
        composable<MapDestination> {
            MapScreen(
                onShowSnackbar = onShowSnackbar,
                onNavigateToEditPlaces = {
                    navController.navigate(EditPlacesDestination)
                }
            )
        }

        composable<EditPlacesDestination>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = LinearOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = LinearOutSlowInEasing
                    )
                )
            },
        ) {
            EditPlacesScreen(
                onShowSnackbar = onShowSnackbar,
                onNavigateToSaveLocation = { navController.navigate(SaveLocationDestination(it)) },
            )
        }

        dialog<SaveLocationDestination> {
            SaveLocationScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}