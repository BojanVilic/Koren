package com.koren.common.models.family

import com.koren.common.models.user.UserData

data class FamilyMemberUserData(
    val userData: UserData,
    val distance: Int = 0,
    val goingHome: Boolean = false
)