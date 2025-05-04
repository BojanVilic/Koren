package com.koren.common.models.suggestion

import kotlinx.serialization.Serializable

@Serializable
data class SuggestionResponse(
    val id: String = "",
    val primaryText: String = "",
    val secondaryText: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)