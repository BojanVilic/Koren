package com.koren.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.koren.R
import com.koren.account.ui.account.AccountDestination
import com.koren.activity.ui.ActivityDestination
import com.koren.home.ui.home.HomeDestination
import com.koren.map.ui.MapDestination

sealed class TopLevelRoute(
    val route: Any,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val titleTextId: Int
)

data object HomeTopLevelRoute : TopLevelRoute(
    route = HomeDestination,
    selectedIcon = R.drawable.home_selected,
    unselectedIcon = R.drawable.home_unselected,
    titleTextId = R.string.home_label
)

data object MapTopLevelRoute : TopLevelRoute(
    route = MapDestination,
    selectedIcon = R.drawable.map_selected,
    unselectedIcon = R.drawable.map_unselected,
    titleTextId = R.string.map_label
)

data object ActivityTopLevelRoute : TopLevelRoute(
    route = ActivityDestination,
    selectedIcon = R.drawable.activity_selected,
    unselectedIcon = R.drawable.activity_unselected,
    titleTextId = R.string.activity_label
)

data object AccountTopLevelRoute : TopLevelRoute(
    route = AccountDestination,
    selectedIcon = R.drawable.account_selected,
    unselectedIcon = R.drawable.account_unselected,
    titleTextId = R.string.account_label
)

val topLevelRoutes = listOf(
    HomeTopLevelRoute,
    MapTopLevelRoute,
    ActivityTopLevelRoute,
    AccountTopLevelRoute
)
