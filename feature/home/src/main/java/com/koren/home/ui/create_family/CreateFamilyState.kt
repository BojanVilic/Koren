package com.koren.home.ui.create_family

import android.net.Uri

sealed interface CreateFamilyUiState {
    data object CreatingFamily: CreateFamilyUiState
    data class Error(val throwable: Throwable?): CreateFamilyUiState
    data object FamilyCreated: CreateFamilyUiState

    data class Step(
        val photoUri: Uri? = null,
        val familyName: String = "",
        val currentStep: Int = 0,
        val eventSink: (CreateFamilyEvent) -> Unit
    ): CreateFamilyUiState {
        val isStepValid: Boolean
            get() = when (currentStep) {
                0 -> photoUri != null
                1 -> familyName.isNotBlank()
                else -> false
            }

        val totalSteps: Int = 2
    }
}

sealed interface CreateFamilyEvent {
    data class SetPhotoUri(val uri: Uri?): CreateFamilyEvent
    data class SetFamilyName(val name: String): CreateFamilyEvent
    data object NextStep: CreateFamilyEvent
    data object PreviousStep: CreateFamilyEvent
    data object CreateFamily: CreateFamilyEvent
}