package com.koren.manage_familiy.edit_member

import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.common.util.Constants.DEFAULT_FREQUENCY_OPTIONS
import com.koren.common.util.Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface EditMemberUiState : UiState {
    data object Loading : EditMemberUiState
    data class Shown(
        val memberDetails: UserData = UserData(),
        val locationUpdateFrequencyOptions: List<Int> = DEFAULT_FREQUENCY_OPTIONS,
        val selectedRole: FamilyRole = FamilyRole.NONE,
        val selectedFrequency: Int = DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS,
        val areYouSureActive: Boolean = false,
        val removingInProgress: Boolean = false,
        override val eventSink: (EditMemberUiEvent) -> Unit
    ) : EditMemberUiState, EventHandler<EditMemberUiEvent>
}

sealed interface EditMemberUiEvent : UiEvent {
    data class UpdateFamilyRole(val familyRole: FamilyRole) : EditMemberUiEvent
    data class UpdateLocationUpdateFrequency(val frequency: Int) : EditMemberUiEvent
    data class RemoveMemberClicked(val memberId: String) : EditMemberUiEvent
    data object CancelRemoveMemberClicked : EditMemberUiEvent
}

sealed interface EditMemberUiSideEffect : UiSideEffect {
    data class ShowFamilyMemberRemovedMessage(val message: String) : EditMemberUiSideEffect
    data class ShowErrorMessage(val message: String) : EditMemberUiSideEffect
    data class ShowMemberDetailsUpdatedMessage(val message: String) : EditMemberUiSideEffect
}