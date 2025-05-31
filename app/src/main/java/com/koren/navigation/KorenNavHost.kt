@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.carfax.manage_familiy.navigation.manageFamilyScreen
import com.carfax.manage_familiy.select_member.SelectMemberDestination
import com.koren.MainActivityViewModel
import com.koren.account.ui.account.AccountDestination
import com.koren.account.ui.navigation.accountScreen
import com.koren.activity.ui.ActivityDestination
import com.koren.activity.ui.activityScreen
import com.koren.auth.navigation.AuthGraph
import com.koren.auth.navigation.authScreen
import com.koren.calendar.navigation.calendarScreen
import com.koren.calendar.ui.add_entry.AddEntryDestination
import com.koren.calendar.ui.add_entry.AddEntryScreen
import com.koren.calendar.ui.calendar.CalendarDestination
import com.koren.chat.navigation.chatScreen
import com.koren.chat.ui.chat.ChatDestination
import com.koren.common.models.calendar.Day
import com.koren.designsystem.components.bottom_sheet.BottomSheetNavigator
import com.koren.designsystem.components.bottom_sheet.ModalBottomSheetLayout
import com.koren.designsystem.components.bottom_sheet.bottomSheet
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home.HomeDestination
import com.koren.invitation.navigation.invitationScreen
import com.koren.invitation.ui.InvitationDestination
import com.koren.map.navigation.mapScreen
import com.koren.map.ui.MapDestination
import com.koren.onboarding.navigation.OnboardingGraph
import com.koren.onboarding.navigation.onboardingScreen
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun KorenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel,
    onShowSnackbar: suspend (message: String) -> Unit,
    bottomSheetNavigator: BottomSheetNavigator
) {
    val uiState = mainActivityViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination: Any = when {
        uiState.value is MainActivityUiState.LoggedOut -> AuthGraph
        uiState.value is MainActivityUiState.Success && (uiState.value as MainActivityUiState.Success).userData.familyId.isBlank() -> OnboardingGraph
        else -> HomeGraph
    }

    if (uiState.value !is MainActivityUiState.Loading) {
        ModalBottomSheetLayout(
            modifier = modifier,
            bottomSheetNavigator = bottomSheetNavigator,
            dragHandle = null
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                authScreen(
                    navController = navController,
                    onSignInSuccess = {
                        navController.navigate(HomeGraph) {
                            popUpTo(HomeGraph) { inclusive = true }
                        }
                    },
                    onSignUpSuccess = {
                        navController.navigate(OnboardingGraph) {
                            popUpTo(OnboardingGraph) { inclusive = true }
                        }
                    },
                    onShowSnackbar = onShowSnackbar
                )
                homeScreen(
                    navController = navController,
                    inviteFamilyMember = {
                        navController.navigate(InvitationDestination)
                    },
                    onShowSnackbar = onShowSnackbar,
                    openAddCalendarEntry = { day ->
                        navController.navigate(
                            AddEntryDestination(
                                dayOfMonth = day.dayOfMonth ?: 0,
                                dayOfWeek = day.dayOfWeek?.value ?: 0,
                                localDate = day.localDate?.toString() ?: ""
                            )
                        )
                    },
                    navigateAndFindOnMap = { userId ->
                        navController.navigateToMapWithCoordinates(userId)
                    },
                    navigateToChat = {
                        navController.navigate(ChatDestination)
                    }
                )
                onboardingScreen(
                    navController = navController,
                    onNavigateToHome = { navController.navigate(HomeDestination) }
                )
                mapScreen(
                    navController = navController,
                    onShowSnackbar = onShowSnackbar
                )
                activityScreen(
                    navController = navController,
                    navigateToCalendar = { navController.navigate(CalendarDestination) }
                )
                accountScreen(
                    navController = navController,
                    onLogOut = {
                        navController.navigate(AuthGraph) {
                            popUpTo(AuthGraph) { inclusive = true }
                        }
                    },
                    onShowSnackbar = onShowSnackbar,
                    navigateToActivity = {
                        navController.navigateToTopLevelDestination(ActivityTopLevelRoute)
                    },
                    navigateToManageFamily = {
                        navController.navigate(SelectMemberDestination)
                    }
                )
                invitationScreen(navController = navController)
                calendarScreen(
                    navController = navController,
                    onShowSnackbar = onShowSnackbar
                )
                chatScreen(
                    navController = navController,
                    onShowSnackbar = onShowSnackbar
                )
                manageFamilyScreen(
                    navController = navController,
                    onShowSnackbar = onShowSnackbar,
                    onNavigateToAddNewMember = {
                        navController.navigate(InvitationDestination)
                    }
                )
                bottomSheet<AddEntryDestination> { backStackEntry ->
                    val addEntryDestination = backStackEntry.toRoute<AddEntryDestination>()
                    AddEntryScreen(
                        day = Day(
                            dayOfMonth = addEntryDestination.dayOfMonth,
                            dayOfWeek = DayOfWeek.of(addEntryDestination.dayOfWeek),
                            localDate = LocalDate.parse(addEntryDestination.localDate)
                        ),
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

fun NavHostController.navigateToTopLevelDestination(topLevelRoute: TopLevelRoute) {
    val navController = this
    val topLevelNavOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    when (topLevelRoute) {
        is HomeTopLevelRoute -> navController.navigate(HomeDestination, topLevelNavOptions)
        is MapTopLevelRoute -> navController.navigate(MapDestination(), topLevelNavOptions)
        is ActivityTopLevelRoute -> navController.navigate(ActivityDestination, topLevelNavOptions)
        is AccountTopLevelRoute -> navController.navigate(AccountDestination, topLevelNavOptions)
    }
}

fun NavHostController.navigateToMapWithCoordinates(
    userId: String
) {
    val navController = this
    val mapNavOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
    navController.navigate(MapDestination(userId), mapNavOptions)
}