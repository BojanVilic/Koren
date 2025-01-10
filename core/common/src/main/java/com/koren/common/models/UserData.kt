package com.koren.common.models

data class UserData(
    val id: String = "",
    val familyId: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val familyRole: FamilyRole = FamilyRole.NONE,
    val lastLocation: LatLng? = null
)