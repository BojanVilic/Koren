package com.koren.common.models.activity

import com.koren.common.models.user.UserData

data class LocationActivity(
    val id: String = "",
    val userId: String = "",
    val familyId: String = "",
    val createdAt: Long = 0,
    val locationName: String = "",
    val inTransit: Boolean = false
)

data class UserLocationActivity(
    val id: String = "",
    val userData: UserData? = null,
    val familyId: String = "",
    val createdAt: Long = 0,
    val locationName: String = "",
    val inTransit: Boolean = false
)