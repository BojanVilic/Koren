package com.koren.navigation

import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface MainActivityUiState : UiState {
    data object Loading : MainActivityUiState
    data class Success(
        val userData: UserData,
        override val eventSink: (MainActivityUiEvent) -> Unit
    ) : MainActivityUiState, EventHandler<MainActivityUiEvent>

    data class Error(val message: String) : MainActivityUiState
    data object LoggedOut : MainActivityUiState

    fun shouldKeepSplashScreen() = this is Loading
}

sealed interface MainActivityUiEvent : UiEvent {

}