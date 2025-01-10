package com.koren.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.koren.common.util.Destination
import com.koren.home.ui.HomeDestination
import com.koren.home.ui.HomeScreen
import com.koren.home.ui.qr.QRAcceptInvitationDestination
import com.koren.home.ui.qr.QRAcceptInvitationScreen
import com.koren.home.ui.sent_invitations.SentInvitationsDestination
import com.koren.home.ui.sent_invitations.SentInvitationsScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeGraph : Destination

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    inviteFamilyMember: () -> Unit,
    createFamily: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<HomeGraph>(
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                inviteFamilyMember = inviteFamilyMember,
                createFamily = createFamily,
                sentInvitations = { navController.navigate(SentInvitationsDestination) }
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
                onNavigateToHome = { errorMessage ->
                    onShowSnackbar(errorMessage)
                    navController.navigateUp()
                }
            )
        }

        composable<SentInvitationsDestination> {
            SentInvitationsScreen()
        }
    }
}