package com.koren.common.models.activity

data class InvitationActivity(
    override val id: String,
    override val userId: String,
    override val createdAt: Long,
    val invitationId: String
): BaseActivity