package com.koren.common.models

data class UserData(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val hasFamily: Boolean = false
)