package com.koren.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val haptics = LocalHapticFeedback.current

        topLevelRoutes.forEach { item ->
            val selected = currentDestination?.getAllParentGraphs()?.any { it.startDestinationRoute == item.route::class.qualifiedName } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = if (selected) painterResource(item.selectedIcon) else painterResource(item.unselectedIcon),
                        contentDescription = stringResource(item.titleTextId)
                    )
                },
                label = { Text(text = stringResource(item.titleTextId)) },
                selected = selected,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    if (selected) return@NavigationBarItem
                    navController.navigate(item.route) {
                        popUpTo(item.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavDestination.getAllParentGraphs(): Set<NavGraph> {
    val parents = mutableSetOf<NavGraph>()
    var currentParent = parent
    while (currentParent != null) {
        parents.add(currentParent)
        currentParent = currentParent.parent
    }
    return parents
}

