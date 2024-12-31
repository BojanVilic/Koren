package com.koren.common.services

import com.koren.common.models.UserData
import kotlinx.coroutines.flow.Flow

interface UserSession {
    val isLoggedIn: Boolean
    val currentUser: Flow<UserData>
    suspend fun updateUserDataOnLogin()
}