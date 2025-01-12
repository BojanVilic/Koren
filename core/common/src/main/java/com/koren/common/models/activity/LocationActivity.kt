package com.koren.common.models.activity

data class LocationActivity(
    override val id: String,
    override val userId: String,
    override val createdAt: Long,
    val locationName: String
): BaseActivity
