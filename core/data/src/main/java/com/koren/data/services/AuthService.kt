package com.koren.data.services

interface AuthService {
    suspend fun signIn(signInMethod: SignInMethod): Result<Unit>
    suspend fun signUp(email: String, password: String, result: (Result<Unit>) -> Unit)
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String)
}