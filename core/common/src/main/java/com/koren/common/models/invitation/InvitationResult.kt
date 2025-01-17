package com.koren.common.models.invitation

data class InvitationResult(
    val invitationId: String = "",
    val familyId: String = "",
    val senderName: String = "",
    val invitationCode: String = "",
    val invitationLink: String = ""
)