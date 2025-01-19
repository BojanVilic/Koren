package com.koren.map.ui

import com.google.maps.android.compose.MarkerState
import com.koren.common.models.user.UserData

data class UiLocationMakerUserData(
    val markerState: MarkerState,
    val userData: UserData
)


