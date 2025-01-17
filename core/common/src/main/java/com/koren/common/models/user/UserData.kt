package com.koren.common.models.user

import com.koren.common.models.family.FamilyRole

data class UserData(
    val id: String = "",
    val familyId: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val familyRole: FamilyRole = FamilyRole.NONE,
    val lastLocation: UserLocation? = null
)