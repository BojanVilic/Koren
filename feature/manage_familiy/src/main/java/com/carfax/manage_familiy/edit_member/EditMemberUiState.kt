package com.carfax.manage_familiy.edit_member

import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.common.util.Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

val defaultFrequencyOptions = listOf(1, 5, 15, 30, 60)

sealed interface EditMemberUiState : UiState {
    data object Loading : EditMemberUiState
    data class Shown(
        val memberDetails: UserData = UserData(),
        val locationUpdateFrequencyOptions: List<Int> = defaultFrequencyOptions,
        val selectedRole: FamilyRole = FamilyRole.NONE,
        val selectedFrequency: Int = DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS,
        val areYouSureActive: Boolean = false,
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

}