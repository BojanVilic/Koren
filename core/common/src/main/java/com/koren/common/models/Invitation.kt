package com.koren.common.models

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

fun Long.toRelativeTime(
    locale: Locale = Locale.getDefault(),
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val now = LocalDate.now(zoneId)
    val date = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val time = Instant.ofEpochMilli(this).atZone(zoneId).toLocalTime()

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)

    return when {
        date.isEqual(now) -> {
            time.format(timeFormatter)
        }
        date.isEqual(now.minusDays(1)) -> {
            "Yesterday at ${time.format(timeFormatter)}"
        }
        else -> {
            val dayOfWeek = date.dayOfWeek.toString().lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(locale) else it.toString()
            }
            "$dayOfWeek at ${time.format(timeFormatter)}"
        }
    }
}