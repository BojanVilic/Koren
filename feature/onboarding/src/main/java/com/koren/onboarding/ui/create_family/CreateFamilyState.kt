package com.koren.onboarding.ui.create_family

import android.net.Uri
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface CreateFamilyUiState : UiState {
    data object Loading: CreateFamilyUiState
    data object CreatingFamily: CreateFamilyUiState
    data class Error(val errorMessage: String): CreateFamilyUiState
    data object FamilyCreated: CreateFamilyUiState

    data class Step(
        val photoUri: Uri? = null,
        val familyName: String = "",
        val currentStep: Int = 0,
        override val eventSink: (CreateFamilyEvent) -> Unit
    ): CreateFamilyUiState, EventHandler<CreateFamilyEvent> {
        val isStepValid: Boolean
            get() = when (currentStep) {
                0 -> photoUri != null
                1 -> familyName.isNotBlank()
                else -> false
            }

        val totalSteps: Int = 2
    }
}

sealed interface CreateFamilyEvent : UiEvent {
    data class SetPhotoUri(val uri: Uri?): CreateFamilyEvent
    data class SetFamilyName(val name: String): CreateFamilyEvent
    data object NextStep: CreateFamilyEvent
    data object PreviousStep: CreateFamilyEvent
    data object CreateFamily: CreateFamilyEvent
}