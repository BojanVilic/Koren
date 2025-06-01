package com.koren.navigation

import com.koren.common.models.user.UserData
import com.koren.common.util.UiState

sealed interface MainActivityUiState : UiState {
    data object Loading : MainActivityUiState
    data class Success(
        val userData: UserData
    ) : MainActivityUiState

    data object LoggedOut : MainActivityUiState

    fun shouldKeepSplashScreen() = this is Loading
}