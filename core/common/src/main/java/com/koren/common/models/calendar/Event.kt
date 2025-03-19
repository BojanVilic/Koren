package com.koren.common.models.calendar

import com.koren.common.models.user.UserData

data class Event(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val eventStartTime: Long = 0,
    val eventEndTime: Long = 0,
    val allDay: Boolean = false,
    val creatorUserId: String = ""
)

data class EventWithUsers(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val eventStartTime: Long = 0,
    val eventEndTime: Long = 0,
    val allDay: Boolean = false,
    val creator: UserData? = null
)