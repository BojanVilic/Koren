package com.koren.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object DateUtils {
    fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}

data class HourMinute(val hour: Int, val minute: Int) {
    override fun toString(): String {
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }
}

fun HourMinute.isBefore(other: HourMinute): Boolean {
    return hour < other.hour || (hour == other.hour && minute < other.minute)
}

fun HourMinute.isAfter(other: HourMinute): Boolean {
    return hour > other.hour || (hour == other.hour && minute > other.minute)
}

fun HourMinute.toLocalTime(): LocalTime {
    return LocalTime.of(hour, minute)
}