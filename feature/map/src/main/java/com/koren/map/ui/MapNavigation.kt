package com.koren.map.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import kotlinx.serialization.Serializable

@Serializable
object MapGraph : Destination

fun NavGraphBuilder.mapScreen(
    navController: NavHostController
) {
    navigation<MapGraph>(
        startDestination = MapDestination
    ) {
        composable<MapDestination> {
            MapScreen()
        }
    }
}