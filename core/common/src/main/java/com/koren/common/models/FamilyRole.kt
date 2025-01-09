package com.koren.common.models

enum class FamilyRole {
    PARENT,
    CHILD,
    GUARDIAN,
    GUEST,
    OTHER,
    NONE
}

val FamilyRole.isModerator: Boolean
    get() = this == FamilyRole.PARENT || this == FamilyRole.GUARDIAN