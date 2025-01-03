package com.koren.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationResult
import com.koren.common.services.UserSession
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userSession: UserSession,
    private val invitationRepository: InvitationRepository
): ViewModel() {

    var invitations by mutableStateOf(emptyList<Invitation>())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.getPendingInvitations().collect {
                invitations = it
            }
        }
    }

    val currentUser = userSession.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun acceptInvitation(invitation: Invitation) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.acceptInvitation(invitation)
        }
    }

    fun declineInvitation(invitationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(invitationCode)
        }
    }
}