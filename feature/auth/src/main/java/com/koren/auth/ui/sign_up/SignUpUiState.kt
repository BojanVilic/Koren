package com.koren.auth.ui.sign_up

import android.net.Uri
import com.koren.common.util.Event
import com.koren.common.util.EventHandler
import com.koren.common.util.SideEffect
import com.koren.common.util.UiState

sealed interface SignUpUiState : UiState {
    data class Shown(
        val firstName: String = "",
        val lastName: String = "",
        val imageUri: Uri? = null,
        val email: String = "",
        val password: String = "",
        val emailErrorMessage: String = "",
        val passwordErrorMessage: String = "",
        val genericErrorMessage: String = "",
        val showPassword: Boolean = false,
        override val eventSink: (SignUpEvent) -> Unit
    ): SignUpUiState, EventHandler<SignUpEvent> {
        val isSignUpButtonEnabled: Boolean
            get() =
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                firstName.isNotEmpty() &&
                lastName.isNotEmpty()

        val displayName: String
            get() = "$firstName $lastName"
    }
}

sealed interface SignUpEvent : Event {
    data object SignUpButtonClicked : SignUpEvent
    data class FirstNameChanged(val firstName: String) : SignUpEvent
    data class LastNameChanged(val lastName: String) : SignUpEvent
    data class EmailChanged(val email: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class SetImageUri(val imageUri: Uri) : SignUpEvent
    data object ShowPasswordClicked : SignUpEvent
    data object SignInClicked : SignUpEvent
}

sealed interface SignUpSideEffect : SideEffect {
    data object NavigateToHome : SignUpSideEffect
    data object NavigateBack : SignUpSideEffect
    data class ShowGenericMessage(val message: String) : SignUpSideEffect
}