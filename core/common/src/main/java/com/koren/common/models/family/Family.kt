package com.koren.common.models.family

data class Family(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val familyPortrait: String = ""
)