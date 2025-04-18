package com.koren.common.models.family

data class Family(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val familyPortrait: String = "",
    val savedLocations: List<SavedLocation> = emptyList(),
    val callHomeRequests: Map<String, CallHomeRequest> = emptyMap(),
    val homeLat: Double = 0.0,
    val homeLng: Double = 0.0
)
