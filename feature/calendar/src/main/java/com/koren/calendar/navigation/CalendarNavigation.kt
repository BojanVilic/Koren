package com.koren.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.calendar.ui.calendar.CalendarDestination
import com.koren.calendar.ui.calendar.CalendarScreen
import kotlinx.serialization.Serializable

@Serializable
object CalendarGraph

fun NavGraphBuilder.calendarScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit,
) {
    navigation<CalendarGraph>(
        startDestination = CalendarDestination
    ) {
        composable<CalendarDestination> {
            CalendarScreen(
                onShowSnackbar = onShowSnackbar,
            )
        }
    }
}