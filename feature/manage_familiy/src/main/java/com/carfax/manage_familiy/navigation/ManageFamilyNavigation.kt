package com.carfax.manage_familiy.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.carfax.manage_familiy.edit_member.EditMemberDestination
import com.carfax.manage_familiy.edit_member.EditMemberScreen
import com.carfax.manage_familiy.select_member.SelectMemberDestination
import com.carfax.manage_familiy.select_member.SelectMemberScreen
import com.koren.designsystem.components.bottom_sheet.bottomSheet
import kotlinx.serialization.Serializable

@Serializable
object ManageFamilyGraph

private const val FAMILY_MEMBER_REMOVED_MESSAGE_KEY = "familyMemberRemovedMessage"

fun NavGraphBuilder.manageFamilyScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit,
    onNavigateToAddNewMember: () -> Unit
) {
    navigation<ManageFamilyGraph>(
        startDestination = SelectMemberDestination
    ) {
        composable<SelectMemberDestination> { entry ->
            val message = entry.savedStateHandle.get<String>(FAMILY_MEMBER_REMOVED_MESSAGE_KEY)
            LaunchedEffect(message) {
                if (message != null) {
                    onShowSnackbar(message)
                    entry.savedStateHandle.remove<String>(FAMILY_MEMBER_REMOVED_MESSAGE_KEY)
                }
            }
            SelectMemberScreen(
                onShowSnackbar = onShowSnackbar,
                onNavigateToEditMember = { memberId ->
                    navController.navigate(EditMemberDestination(memberId))
                },
                onNavigateToAddNewMember = onNavigateToAddNewMember
            )
        }
        bottomSheet<EditMemberDestination> {
            EditMemberScreen(
                onFamilyMemberRemoved = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(FAMILY_MEMBER_REMOVED_MESSAGE_KEY, message)
                    navController.popBackStack()
                }
            )
        }
    }
}