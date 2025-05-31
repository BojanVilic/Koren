package com.koren.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.onboarding.ui.create_family.CreateFamilyDestination
import com.koren.onboarding.ui.create_family.CreateFamilyScreen
import com.koren.onboarding.ui.create_or_join_family.CreateOrJoinFamilyDestination
import com.koren.onboarding.ui.create_or_join_family.CreateOrJoinFamilyScreen
import com.koren.onboarding.ui.join_family.JoinFamilyDestination
import com.koren.onboarding.ui.join_family.JoinFamilyScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingGraph

fun NavGraphBuilder.onboardingScreen(
    navController: NavHostController,
    onNavigateToHome: () -> Unit
) {
    navigation<OnboardingGraph>(
        startDestination = CreateOrJoinFamilyDestination
    ) {
        composable<CreateOrJoinFamilyDestination> {
            CreateOrJoinFamilyScreen(
                onNavigateToOnboarding = { navController.navigate(CreateFamilyDestination) },
                onNavigateToPendingInvitationsScreen = { navController.navigate(JoinFamilyDestination) }
            )
        }

        composable<CreateFamilyDestination> {
            CreateFamilyScreen(
                onNavigateToHome = onNavigateToHome
            )
        }

        composable<JoinFamilyDestination> {
            JoinFamilyScreen(
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}