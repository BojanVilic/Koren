package com.koren.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        topLevelRoutes.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(route = item.route::class)
            } == true

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

