package com.koren.home.ui.sent_invitations

import com.koren.common.models.invitation.Invitation

data class UiSentInvitation(
    val expanded: Boolean = false,
    val invitation: Invitation = Invitation()
)
