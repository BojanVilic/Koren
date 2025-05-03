package com.koren.map.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.koren.designsystem.components.bottom_sheet.bottomSheet
import com.koren.map.ui.MapDestination
import com.koren.map.ui.MapScreen
import com.koren.map.ui.edit_places.EditPlacesDestination
import com.koren.map.ui.edit_places.EditPlacesScreen
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
                userId = mapDestination.userId,
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
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}