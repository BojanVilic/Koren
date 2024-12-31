package com.koren.common.models

data class Invitation(
    val id: String = "",
    val familyId: String = "",
    val senderId: String = "",
    val invitationLink: String = "",
    val invitationCode: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val expirationDate: Long = 0,
    val createdAt: Long = 0
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED
}