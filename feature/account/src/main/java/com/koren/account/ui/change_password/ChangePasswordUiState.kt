package com.koren.account.ui.change_password

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChangePasswordUiState : UiState {
    data object Loading : ChangePasswordUiState
    data class EmailSent(
        val email: String,
        val close: () -> Unit
    ) : ChangePasswordUiState
    data class Shown(
        val email: String = "",
        val emailSent: Boolean = false,
        override val eventSink: (ChangePasswordUiEvent) -> Unit
    ) : ChangePasswordUiState, EventHandler<ChangePasswordUiEvent>
}

sealed interface ChangePasswordUiEvent : UiEvent {
    data object SendResetPasswordEmail : ChangePasswordUiEvent
    data object Close : ChangePasswordUiEvent
    data class EmailChanged(val email: String) : ChangePasswordUiEvent
}

sealed interface ChangePasswordSideEffect : UiSideEffect {
    data object Close : ChangePasswordSideEffect
}