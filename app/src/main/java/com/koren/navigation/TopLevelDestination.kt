package com.koren.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.koren.R
import com.koren.account.ui.account.AccountDestination
import com.koren.activity.ui.ActivityDestination
import com.koren.designsystem.icon.AccountSelected
import com.koren.designsystem.icon.AccountUnselected
import com.koren.designsystem.icon.ActivitySelected
import com.koren.designsystem.icon.ActivityUnselected
import com.koren.designsystem.icon.HomeSelected
import com.koren.designsystem.icon.HomeUnselected
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.MapSelected
import com.koren.designsystem.icon.MapUnselected
import com.koren.home.ui.home.HomeDestination
import com.koren.map.ui.MapDestination

sealed class TopLevelRoute(
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val titleTextId: Int
)

data object HomeTopLevelRoute : TopLevelRoute(
    route = HomeDestination,
    selectedIcon = KorenIcons.HomeSelected,
    unselectedIcon = KorenIcons.HomeUnselected,
    titleTextId = R.string.home_label
)

data object MapTopLevelRoute : TopLevelRoute(
    route = MapDestination(),
    selectedIcon = KorenIcons.MapSelected,
    unselectedIcon = KorenIcons.MapUnselected,
    titleTextId = R.string.map_label
)

data object ActivityTopLevelRoute : TopLevelRoute(
    route = ActivityDestination,
    selectedIcon = KorenIcons.ActivitySelected,
    unselectedIcon = KorenIcons.ActivityUnselected,
    titleTextId = R.string.activity_label
)

data object AccountTopLevelRoute : TopLevelRoute(
    route = AccountDestination,
    selectedIcon = KorenIcons.AccountSelected,
    unselectedIcon = KorenIcons.AccountUnselected,
    titleTextId = R.string.account_label
)

val topLevelRoutes = listOf(
    HomeTopLevelRoute,
    MapTopLevelRoute,
    ActivityTopLevelRoute,
    AccountTopLevelRoute
)
