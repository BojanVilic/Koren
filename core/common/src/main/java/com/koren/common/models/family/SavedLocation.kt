package com.koren.common.models.family

data class SavedLocation(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val iconName: String = ""
)