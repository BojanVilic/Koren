package com.koren.common.models.family

import com.koren.common.models.user.UserData

data class CallHomeRequest(
    val targetUserId: String = "",
    val requesterId: String = "",
    val timestamp: Long = 0,
    val status: CallHomeRequestStatus = CallHomeRequestStatus.NONE
)

data class CallHomeRequestWithUser(
    val requester: UserData,
    val timestamp: Long = 0,
    val status: CallHomeRequestStatus = CallHomeRequestStatus.NONE
)

enum class CallHomeRequestStatus {
    REQUESTED,
    ACCEPTED,
    REJECTED,
    EXPIRED,
    NONE
}