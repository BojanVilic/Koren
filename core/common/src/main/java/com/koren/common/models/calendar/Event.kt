package com.koren.common.models.calendar

data class Event(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val eventStartTime: Long = 0,
    val eventEndTime: Long = 0,
    val isAllDay: Boolean = false,
    val creatorUserId: String = ""
)