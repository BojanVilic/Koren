package com.koren.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.koren.calendar.ui.add_entry.AddEntryDestination
import com.koren.calendar.ui.add_entry.AddEntryScreen
import com.koren.calendar.ui.calendar.CalendarDestination
import com.koren.calendar.ui.calendar.CalendarScreen
import com.koren.calendar.ui.day_details.DayDetailsDestination
import com.koren.calendar.ui.day_details.DayDetailsScreen
import com.koren.common.models.calendar.Day
import com.koren.designsystem.components.bottom_sheet.bottomSheet
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

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
                openDayDetails = {
                    navController.navigate(
                        DayDetailsDestination(
                            dayOfMonth = it.dayOfMonth ?: 0,
                            dayOfWeek = it.dayOfWeek?.value ?: 0,
                            localDate = it.localDate?.toString() ?: ""
                        )
                    )
                }
            )
        }
        bottomSheet<DayDetailsDestination> { backStackEntry ->
            val dayDetailsDestination = backStackEntry.toRoute<DayDetailsDestination>()
            DayDetailsScreen(
                day = Day(
                    dayOfMonth = dayDetailsDestination.dayOfMonth,
                    dayOfWeek = DayOfWeek.of(dayDetailsDestination.dayOfWeek),
                    localDate = LocalDate.parse(dayDetailsDestination.localDate)
                ),
                openAddEntry = {
                    navController.navigate(
                        AddEntryDestination(
                            dayOfMonth = it.dayOfMonth ?: 0,
                            dayOfWeek = it.dayOfWeek?.value ?: 0,
                            localDate = it.localDate?.toString() ?: ""
                        )
                    )
                }
            )
        }
        bottomSheet<AddEntryDestination> { backStackEntry ->
            val addEntryDestination = backStackEntry.toRoute<AddEntryDestination>()
            AddEntryScreen(
                day = Day(
                    dayOfMonth = addEntryDestination.dayOfMonth,
                    dayOfWeek = DayOfWeek.of(addEntryDestination.dayOfWeek),
                    localDate = LocalDate.parse(addEntryDestination.localDate)
                ),
                onDismiss = { navController.popBackStack() },
            )
        }
    }
}