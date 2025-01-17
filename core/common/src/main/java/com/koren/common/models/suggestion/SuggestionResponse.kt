package com.koren.common.models.suggestion

data class SuggestionResponse(
    val primaryText: String = "",
    val secondaryText: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)