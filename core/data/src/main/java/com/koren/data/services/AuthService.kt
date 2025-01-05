package com.koren.data.services

interface AuthService {
    suspend fun signIn(signInMethod: SignInMethod): Result<Unit>
    suspend fun signOut()
}