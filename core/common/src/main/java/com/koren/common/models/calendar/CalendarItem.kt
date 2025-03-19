package com.koren.common.models.calendar

sealed class CalendarItem {
    data class EventItem(val event: EventWithUsers) : CalendarItem()
    data class TaskItem(val task: TaskWithUsers) : CalendarItem()
    data object None : CalendarItem()
}