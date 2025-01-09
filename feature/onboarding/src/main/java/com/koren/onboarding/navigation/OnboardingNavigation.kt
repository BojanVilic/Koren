package com.koren.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.common.util.Destination
import com.koren.onboarding.ui.create_family.CreateFamilyDestination
import com.koren.onboarding.ui.create_family.CreateFamilyScreen
import com.koren.onboarding.ui.onboarding.OnboardingDestination
import com.koren.onboarding.ui.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingGraph : Destination

fun NavGraphBuilder.onboardingScreen(
    navController: NavHostController,
    onNavigateToHome: () -> Unit
) {
    navigation<OnboardingGraph>(
        startDestination = CreateFamilyDestination
    ) {
        composable<CreateFamilyDestination> {
            CreateFamilyScreen(
                onNavigateToHome = onNavigateToHome
            )
        }
        composable<OnboardingDestination> {
            OnboardingScreen(
                joinFamily = {},
                createFamily = { navController.navigate(CreateFamilyDestination) }
            )
        }
    }
}