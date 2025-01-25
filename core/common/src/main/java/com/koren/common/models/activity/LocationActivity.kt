package com.koren.common.models.activity

data class LocationActivity(
    val id: String = "",
    val userId: String = "",
    val userDisplayName: String = "",
    val familyId: String = "",
    val createdAt: Long = 0,
    val locationName: String = "",
    val inTransit: Boolean = false
)
