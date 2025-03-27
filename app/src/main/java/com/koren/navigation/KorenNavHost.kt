@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.koren.MainActivityViewModel
import com.koren.account.ui.account.AccountDestination
import com.koren.account.ui.navigation.accountScreen
import com.koren.activity.ui.ActivityDestination
import com.koren.activity.ui.activityScreen
import com.koren.auth.navigation.AuthGraph
import com.koren.auth.navigation.authScreen
import com.koren.calendar.navigation.calendarScreen
import com.koren.calendar.ui.calendar.CalendarDestination
import com.koren.home.navigation.HomeGraph
import com.koren.home.navigation.homeScreen
import com.koren.home.ui.home.HomeDestination
import com.koren.home.ui.home.member_details.MemberDetails
import com.koren.home.ui.home.member_details.MemberDetailsScreen
import com.koren.invitation.navigation.invitationScreen
import com.koren.invitation.ui.InvitationDestination
import com.koren.map.ui.MapDestination
import com.koren.map.ui.mapScreen
import com.koren.onboarding.navigation.OnboardingGraph
import com.koren.onboarding.navigation.onboardingScreen

@Composable
fun KorenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel,
    onShowSnackbar: suspend (message: String) -> Unit,
    setMainActivityBottomSheetContent: (MainActivityBottomSheetContent) -> Unit,
    bottomSheetNavigator: BottomSheetNavigator
) {
    val uiState = mainActivityViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination: Any = when {
        uiState.value is MainActivityUiState.LoggedOut -> AuthGraph
        else -> HomeGraph
    }

    if (uiState.value !is MainActivityUiState.Loading) {
        ModalBottomSheetLayout(
            modifier = modifier,
            bottomSheetNavigator = bottomSheetNavigator,
            sheetBackgroundColor = BottomSheetDefaults.ContainerColor,
            sheetShape = BottomSheetDefaults.ExpandedShape
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                authScreen(
                    navController = navController,
                    onSignInSuccess = {
                        mainActivityViewModel.onSignInSuccess()
                        navController.navigate(HomeGraph)
                    },
                    onShowSnackbar = onShowSnackbar
                )
                homeScreen(
                    navController = navController,
                    inviteFamilyMember = {
                        navController.navigate(InvitationDestination)
                    },
                    createFamily = { navController.navigate(OnboardingGraph) },
                    onShowSnackbar = onShowSnackbar,
                    openAddCalendarEntry = {
                        setMainActivityBottomSheetContent(
                            MainActivityBottomSheetContent.AddCalendarEntry(it)
                        )
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
                    }
                )
                invitationScreen(navController = navController)
                calendarScreen(navController = navController, onShowSnackbar = onShowSnackbar)
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
        is MapTopLevelRoute -> navController.navigate(MapDestination, topLevelNavOptions)
        is ActivityTopLevelRoute -> navController.navigate(ActivityDestination, topLevelNavOptions)
        is AccountTopLevelRoute -> navController.navigate(AccountDestination, topLevelNavOptions)
    }
}