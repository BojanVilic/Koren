package com.koren.common.models

data class FamilyMember(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profilePicture: String,
    val familyRole: FamilyRole,
    val isModerator: Boolean
)