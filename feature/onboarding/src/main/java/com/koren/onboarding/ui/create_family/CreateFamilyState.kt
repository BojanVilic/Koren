package com.koren.onboarding.ui.create_family

import android.net.Uri
import com.koren.common.models.invitation.InvitationResult
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserLocation
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

enum class CreateFamilyStep {
    ADD_FAMILY_PORTRAIT,
    ADD_FAMILY_NAME,
    ADD_HOUSE_ADDRESS,
    INVITE_FAMILY_MEMBERS
}

sealed interface CreateFamilyUiState : UiState {
    data object Loading: CreateFamilyUiState
    data object CreatingFamily: CreateFamilyUiState
    data class Error(val errorMessage: String): CreateFamilyUiState
    data object FamilyCreated: CreateFamilyUiState

    data class Step(
        val photoUri: Uri? = null,
        val familyName: String = "",
        val currentStep: CreateFamilyStep = CreateFamilyStep.ADD_FAMILY_PORTRAIT,
        val addressText: String = "",
        val homeAddress: SuggestionResponse? = null,
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
                CreateFamilyStep.ADD_FAMILY_PORTRAIT -> photoUri != null
                CreateFamilyStep.ADD_FAMILY_NAME -> familyName.isNotBlank()
                CreateFamilyStep.ADD_HOUSE_ADDRESS -> homeAddress != null
                CreateFamilyStep.INVITE_FAMILY_MEMBERS -> true
            }

        val totalSteps: Int = CreateFamilyStep.entries.size
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