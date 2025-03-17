package com.koren.common.models.calendar

data class Task(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val taskTimestamp: Long = 0,
    val completed: Boolean = false,
    val creatorUserId: String = "",
    val assigneeUserId: String = ""
)