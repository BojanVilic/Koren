package com.koren.common.models.calendar

import com.koren.common.models.user.UserData

data class Task(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val taskTimestamp: Long = 0,
    val completed: Boolean = false,
    val creatorUserId: String = "",
    val assigneeUserId: String = ""
)

data class TaskWithUsers(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val taskTimestamp: Long = 0,
    val completed: Boolean = false,
    val creator: UserData? = null,
    val assignee: UserData? = null
)