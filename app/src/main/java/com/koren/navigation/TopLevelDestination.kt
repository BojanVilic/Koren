package com.koren.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.koren.R
import com.koren.home.ui.home_screen.HomeDestination
import com.koren.map.ui.MapDestination
import kotlinx.serialization.Serializable


@Serializable
data object ActivityGraph

@Serializable
data object AccountGraph

data class TopLevelRoute<T : Any>(
    val route: T,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val titleTextId: Int
)

val topLevelRoutes = listOf(
    TopLevelRoute(
        route = HomeDestination,
        selectedIcon = R.drawable.home_selected,
        unselectedIcon = R.drawable.home_unselected,
        titleTextId = R.string.home_label
    ),
    TopLevelRoute(
        route = MapDestination,
        selectedIcon = R.drawable.map_selected,
        unselectedIcon = R.drawable.map_unselected,
        titleTextId = R.string.map_label
    ),
    TopLevelRoute(
        route = ActivityGraph,
        selectedIcon = R.drawable.activity_selected,
        unselectedIcon = R.drawable.activity_unselected,
        titleTextId = R.string.activity_label
    ),
    TopLevelRoute(
        route = AccountGraph,
        selectedIcon = R.drawable.account_selected,
        unselectedIcon = R.drawable.account_unselected,
        titleTextId = R.string.account_label
    )
)
