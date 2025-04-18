package com.koren.onboarding.ui.create_family

import android.net.Uri
import com.koren.common.models.invitation.InvitationResult
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserLocation
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
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
        val addressText: String = "",
        val homeAddress: UserLocation? = null,
        val isCreateQRInvitationExpanded: Boolean = false,
        val emailInviteText: String = "",
        val isEmailInviteExpanded: Boolean = false,
        val qrInvitation: InvitationResult? = null,
        val emailInvitation: InvitationResult? = null,
        val emailInvitationLoading: Boolean = false,
        val qrInvitationLoading: Boolean = false,
        val searchQuery: String = "",
        val searchBarExpanded: Boolean = false,
        val locationSuggestions: List<SuggestionResponse> = emptyList(),
        override val eventSink: (CreateFamilyEvent) -> Unit
    ): CreateFamilyUiState, EventHandler<CreateFamilyEvent> {
        val isStepValid: Boolean
            get() = when (currentStep) {
                0 -> photoUri != null
                1 -> familyName.isNotBlank()
                2 -> homeAddress != null
                3 -> true
                else -> false
            }

        val totalSteps: Int = 4
    }
}

sealed interface CreateFamilyEvent : UiEvent {
    data class SetPhotoUri(val uri: Uri?): CreateFamilyEvent
    data class SetFamilyName(val name: String): CreateFamilyEvent
    data class SetHomeAddress(val addressText: String): CreateFamilyEvent
    data object NextStep: CreateFamilyEvent
    data object PreviousStep: CreateFamilyEvent
    data object CreateFamily: CreateFamilyEvent
    data object CreateQRInvitation : CreateFamilyEvent
    data object CollapseCreateQRInvitation : CreateFamilyEvent
    data object InviteViaEmailClick : CreateFamilyEvent
    data class EmailInviteTextChange(val email: String) : CreateFamilyEvent
    data object EmailInviteClick : CreateFamilyEvent
    data class SearchTextChanged(val text: String) : CreateFamilyEvent
    data class LocationSuggestionClicked(val location: SuggestionResponse) : CreateFamilyEvent
    data object ExpandSearchBar : CreateFamilyEvent
    data object CollapseSearchBar : CreateFamilyEvent
}

sealed interface CreateFamilySideEffect : UiSideEffect {
    data class ShowError(val errorMessage: String): CreateFamilySideEffect
    data class GetNewLocationSuggestions(val newQuery: String) : CreateFamilySideEffect
}