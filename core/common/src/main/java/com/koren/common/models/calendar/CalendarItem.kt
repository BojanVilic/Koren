package com.koren.common.models.calendar

sealed class CalendarItem {
    data class EventItem(val event: Event) : CalendarItem()
    data class TaskItem(val task: Task) : CalendarItem()
    data object None : CalendarItem()
}