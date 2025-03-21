package com.koren.common.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun Long.convertToUtcWithLocalOffset(): Long {
        val userTimeZoneOffsetMillis = ZoneOffset.systemDefault().rules.getOffset(Instant.now()).totalSeconds * 1000L
        return this - userTimeZoneOffsetMillis
    }

    fun Long.toLocalDate(atLocalTimeZone: Boolean = false): LocalDate {
        val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
        return Instant.ofEpochMilli(this)
            .atZone(zoneOffset)
            .toLocalDate()
    }

    fun LocalDate?.toEpochMilliDayStart(atLocalTimeZone: Boolean = false): Long {
        val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
        return this?.atStartOfDay(zoneOffset)?.toInstant()?.toEpochMilli()?: 0L
    }

    fun LocalDate?.toEpochMilliDayEnd(atLocalTimeZone: Boolean = false): Long {
        val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
        return this?.atTime(LocalTime.MAX)?.atZone(zoneOffset)?.toInstant()?.toEpochMilli()?: 0L
    }

    fun Long.toLocalTimeZoneTimestamp(): Long {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneOffset.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun Long?.toHumanReadableDate(
        atLocalTimeZone: Boolean = false
    ): String {
        if (this == null) return ""

        return try {
            val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
            val instant = Instant.ofEpochMilli(this)
            val zonedDateTime = instant.atZone(zoneOffset)

            val localDate = zonedDateTime.toLocalDate()

            val formatter = DateTimeFormatter.ofPattern("EEE, d-MMM", Locale.getDefault())
            localDate.format(formatter)
        } catch (e: Exception) {
            println("Error formatting date: ${e.message}")
            ""
        }
    }

    fun Long?.toHumanReadableDateTime(
        atLocalTimeZone: Boolean = false
    ): String {
        if (this == null) return ""

        return try {
            val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
            val instant = Instant.ofEpochMilli(this)
            val zonedDateTime = instant.atZone(zoneOffset)

            val localDateTime = zonedDateTime.toLocalDateTime()

            val formatter = DateTimeFormatter.ofPattern("EEE, d-MMM HH:mm", Locale.getDefault())
            localDateTime.format(formatter)
        } catch (e: Exception) {
            println("Error formatting date: ${e.message}")
            ""
        }
    }


    fun Long?.toTime(
        atLocalTimeZone: Boolean = false
    ): String {
        if (this == null) return ""

        return try {
            val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
            val instant = Instant.ofEpochMilli(this)
            val zonedDateTime = instant.atZone(zoneOffset)

            val localTime = zonedDateTime.toLocalTime()

            val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
            localTime.format(formatter)
        } catch (e: Exception) {
            println("Error formatting date: ${e.message}")
            ""
        }
    }

    fun Pair<Long, Long>.toHumanReadableDateTimeRange(
        atLocalTimeZone: Boolean = false
    ): String {
        val zoneOffset = if (atLocalTimeZone) ZoneOffset.systemDefault() else ZoneOffset.UTC
        val firstZoned = Instant.ofEpochMilli(this.first).atZone(zoneOffset)
        val secondZoned = Instant.ofEpochMilli(this.second).atZone(zoneOffset)

        val firstDay = firstZoned.toLocalDate()
        val secondDay = secondZoned.toLocalDate()

        return if (firstDay == secondDay) {
            // same day -> only time range
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            "${firstZoned.format(timeFormatter)} - ${secondZoned.format(timeFormatter)}"
        } else {
            // different days -> date and time range
            val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d-MMM HH:mm")
            "${firstZoned.format(dateTimeFormatter)} - ${secondZoned.format(dateTimeFormatter)}"
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
}