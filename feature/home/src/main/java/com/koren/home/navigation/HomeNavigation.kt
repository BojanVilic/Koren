package com.koren.home.navigation

import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.koren.common.models.calendar.Day
import com.koren.home.ui.home.HomeDestination
import com.koren.home.ui.home.HomeScreen
import com.koren.home.ui.home.member_details.MemberDetails
import com.koren.home.ui.home.member_details.MemberDetailsScreen
import com.koren.home.ui.qr.QRAcceptInvitationDestination
import com.koren.home.ui.qr.QRAcceptInvitationScreen
import com.koren.home.ui.sent_invitations.SentInvitationsDestination
import com.koren.home.ui.sent_invitations.SentInvitationsScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeGraph

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    inviteFamilyMember: () -> Unit,
    createFamily: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    openAddCalendarEntry: (Day) -> Unit
) {
    navigation<HomeGraph>(
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                inviteFamilyMember = inviteFamilyMember,
                createFamily = createFamily,
                sentInvitations = { navController.navigate(SentInvitationsDestination) },
                onShowSnackbar = onShowSnackbar,
                openAddCalendarEntry = openAddCalendarEntry,
                openMemberDetails = {
                    navController.navigate(MemberDetails(it))
                }
            )
        }

        composable<QRAcceptInvitationDestination>(
            deepLinks = listOf(
                navDeepLink<QRAcceptInvitationDestination>(
                    basePath = "koren://join"
                )
            )
        ) { backStackEntry ->
            QRAcceptInvitationScreen(
                invitationId = backStackEntry.toRoute<QRAcceptInvitationDestination>().invitationId,
                familyId = backStackEntry.toRoute<QRAcceptInvitationDestination>().familyId,
                invitationCode = backStackEntry.toRoute<QRAcceptInvitationDestination>().invitationCode,
                onNavigateToHome = {
                    navController.navigateUp()
                },
                onNavigateToHomeWithError = { errorMessage ->
                    onShowSnackbar(errorMessage)
                    navController.navigateUp()
                }
            )
        }
        composable<SentInvitationsDestination> {
            SentInvitationsScreen()
        }

        bottomSheet<MemberDetails> { backStackEntry ->
            MemberDetailsScreen(
                userId = backStackEntry.toRoute<MemberDetails>().userId
            )
        }
    }
}