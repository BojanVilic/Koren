package com.koren.invitation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.invitation.ui.InvitationDestination
import com.koren.invitation.ui.InvitationScreen
import kotlinx.serialization.Serializable

@Serializable
object InvitationGraph

fun NavGraphBuilder.invitationScreen(
    navController: NavHostController
) {
    navigation<InvitationGraph>(
        startDestination = InvitationDestination
    ) {
        composable<InvitationDestination> {
            InvitationScreen()
        }
    }
}