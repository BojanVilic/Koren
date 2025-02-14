package com.koren.calendar.ui

import java.time.DayOfWeek

data class Day(
    val dayOfMonth: Int?,
    val dayOfWeek: DayOfWeek? = null
)
