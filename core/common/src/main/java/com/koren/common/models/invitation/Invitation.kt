package com.koren.common.models.invitation

import android.text.format.DateUtils
import java.time.Duration
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
    val recipientEmail: String = "",
    val familyName: String = "",
    val senderName: String = ""
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED
}

fun Long?.toHumanReadableDate(
    locale: Locale = Locale.getDefault()
): String {
    if (this == null) return ""

    return try {
        val instant = Instant.ofEpochMilli(this)
        val zonedDateTime = instant.atZone(ZoneId.of("UTC"))

        val localDate = zonedDateTime.toLocalDate()

        val formatter = DateTimeFormatter.ofPattern("EEE, d-MMM", locale)
        localDate.format(formatter)
    } catch (e: Exception) {
        println("Error formatting date: ${e.message}")
        ""
    }
}

fun Long.toRelativeTime(): String {
    val nowMillis = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(
        this,
        nowMillis,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME
    ).toString()
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

fun Invitation.isQRInvitation(): Boolean {
    return recipientEmail.isBlank()
}