package com.koren.navigation

import com.koren.common.models.calendar.Day
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface MainActivityBottomSheetContent {
    data object None : MainActivityBottomSheetContent
    data class AddCalendarEntry(val day: Day) : MainActivityBottomSheetContent
}

sealed interface MainActivityUiState : UiState {
    data object Loading : MainActivityUiState
    data class Success(
        val bottomSheetContent: MainActivityBottomSheetContent = MainActivityBottomSheetContent.None,
        val userData: UserData,
        override val eventSink: (MainActivityUiEvent) -> Unit
    ) : MainActivityUiState, EventHandler<MainActivityUiEvent>

    data class Error(val message: String) : MainActivityUiState
    data object LoggedOut : MainActivityUiState

    fun shouldKeepSplashScreen() = this is Loading
}

sealed interface MainActivityUiEvent : UiEvent {
    data object DismissBottomSheet : MainActivityUiEvent
    data class SetBottomSheetContent(val bottomSheetContent: MainActivityBottomSheetContent) : MainActivityUiEvent
}