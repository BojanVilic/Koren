package com.koren.common.models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Invitation(
    val id: String = "",
    val familyId: String = "",
    val senderId: String = "",
    val invitationLink: String = "",
    val invitationCode: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val expirationDate: Long = 0,
    val createdAt: Long = 0,
    val recipientEmail: String = ""
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED
}

fun Long.toHumanReadableDateTime(
    locale: Locale = Locale.getDefault()
): String {
    return try {
        val instant = Instant.ofEpochMilli(this)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("HH:mm d-MMM-yyyy", locale)
        localDateTime.format(formatter)
    } catch (e: Exception) {
        "Invalid Date"
    }
}