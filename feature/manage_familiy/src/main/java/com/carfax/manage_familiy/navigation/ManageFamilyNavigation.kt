package com.carfax.manage_familiy.navigation

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

fun NavGraphBuilder.manageFamilyScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit
) {
    navigation<ManageFamilyGraph>(
        startDestination = SelectMemberDestination
    ) {
        composable<SelectMemberDestination> {
            SelectMemberScreen(
                onShowSnackbar = onShowSnackbar,
                onNavigateToEditMember = { memberId ->
                    navController.navigate(
                        EditMemberDestination(memberId)
                    )
                }
            )
        }
        bottomSheet<EditMemberDestination> {
            EditMemberScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}