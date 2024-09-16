package com.koren.common.services

import com.koren.common.models.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserSession {
    val isLoggedIn: Boolean
    val currentUser: Flow<UserInfo?>
}