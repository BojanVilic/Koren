package com.koren.common.models.invitation

import java.time.Duration
import java.time.Instant

data class Invitation(
    val id: String = "",
    val familyId: String = "",
    val senderId: String = "",
    val invitationLink: String = "",
    val invitationCode: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val expirationDate: Long = 0,
    val createdAt: Long = 0,
    val recipientEmail: String = "",
    val familyName: String = "",
    val senderName: String = ""
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED
}

fun Invitation.isQRInvitation(): Boolean {
    return recipientEmail.isBlank()
}

fun Invitation.getExpiryText(): String {
    if (status != InvitationStatus.PENDING || expirationDate == 0L) {
        return ""
    }

    val now = Instant.now()
    val expiration = Instant.ofEpochMilli(expirationDate)

    val duration = Duration.between(now, expiration)
    val hours = duration.toHours()

    return if (hours <= 0) {
        "Expired"
    } else {
        val formattedHours = hours.toInt().toString()
        "Expires in $formattedHours hours"
    }
}