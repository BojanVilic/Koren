package com.carfax.manage_familiy.select_member

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectMemberViewModel @Inject constructor(
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val userSession: UserSession
) : MoleculeViewModel<SelectMemberUiEvent, SelectMemberUiState, SelectMemberUiSideEffect>() {

    override fun setInitialState(): SelectMemberUiState = SelectMemberUiState.Loading

    @Composable
    override fun produceState(): SelectMemberUiState {
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")
        val members = getAllFamilyMembersUseCase.invoke().collectAsState(initial = emptyList())
            .value
            .filter { it.id != currentUserId }

        return SelectMemberUiState.Shown(
            members = members,
        ) { event ->
            when (event) {
                is SelectMemberUiEvent.MemberSelected -> _sideEffects.emitSuspended(SelectMemberUiSideEffect.NavigateToEditMember(event.member.id))
                is SelectMemberUiEvent.AddMemberClicked -> Unit
            }
        }
    }
}