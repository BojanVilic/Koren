package com.koren.calendar.ui

import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import java.time.DayOfWeek
import java.time.LocalDate

data class Day(
    val dayOfMonth: Int?,
    val dayOfWeek: DayOfWeek? = null,
    val localDate: LocalDate? = null,
    val tasks: List<Task> = emptyList(),
    val events: List<Event> = emptyList()
)