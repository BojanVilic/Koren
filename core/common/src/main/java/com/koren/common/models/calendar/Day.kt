package com.koren.common.models.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class Day(
    val dayOfMonth: Int? = null,
    val dayOfWeek: DayOfWeek? = null,
    val localDate: LocalDate? = null,
    val tasks: List<Task> = emptyList(),
    val events: List<Event> = emptyList()
)

fun Day.toDayDateMonth(): String {
    return "${dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, $dayOfMonth ${localDate?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())}"
}