package com.carfax.manage_familiy.edit_member

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.koren.common.models.user.UserData
import com.koren.common.util.MoleculeViewModel
import com.koren.domain.GetFamilyMemberUseCase
import com.koren.domain.RemoveMemberFromFamilyUseCase
import com.koren.domain.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFamilyMemberUseCase: GetFamilyMemberUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val removeMemberFromFamilyUseCase: RemoveMemberFromFamilyUseCase
) : MoleculeViewModel<EditMemberUiEvent, EditMemberUiState, EditMemberUiSideEffect>() {

    override fun setInitialState(): EditMemberUiState = EditMemberUiState.Loading

    @Composable
    override fun produceState(): EditMemberUiState {
        val memberId = savedStateHandle.toRoute<EditMemberDestination>().memberId
        val memberDetails by getFamilyMemberUseCase(memberId).collectAsState(initial = UserData())
        var selectedRole by remember { mutableStateOf(memberDetails.familyRole) }
        var selectedFrequency by remember { mutableIntStateOf(memberDetails.locationUpdateFrequencyInMins) }
        var areYouSureActive by remember { mutableStateOf(false) }
        var deletingInProgress by remember { mutableStateOf(false) }

        return EditMemberUiState.Shown(
            memberDetails = memberDetails,
            selectedRole = selectedRole,
            selectedFrequency = selectedFrequency,
            areYouSureActive = areYouSureActive,
            removingInProgress = deletingInProgress
        ) { event ->
            when (event) {
                is EditMemberUiEvent.UpdateFamilyRole -> selectedRole = event.familyRole
                is EditMemberUiEvent.UpdateLocationUpdateFrequency -> selectedFrequency = event.frequency
                is EditMemberUiEvent.RemoveMemberClicked -> {
                    if (areYouSureActive) {
                        deletingInProgress = true
                        removeMemberFromFamily(
                            memberId = event.memberId,
                            resetRemovingInProgress = { deletingInProgress = false }
                        )
                    }
                    else areYouSureActive = true
                }
                is EditMemberUiEvent.CancelRemoveMemberClicked -> areYouSureActive = false
            }
        }
    }

    private fun removeMemberFromFamily(
        memberId: String,
        resetRemovingInProgress: () -> Unit
    ) {
        viewModelScope.launch {
            removeMemberFromFamilyUseCase(memberId)
                .onSuccess {
                    sendSideEffect(EditMemberUiSideEffect.ShowFamilyMemberRemovedMessage("Member removed successfully."))
                    resetRemovingInProgress()
                }
                .onFailure {
                    sendSideEffect(EditMemberUiSideEffect.ShowErrorMessage("There was an error trying to remove a family member. Please try again later."))
                    resetRemovingInProgress()
                }
        }
    }
}
