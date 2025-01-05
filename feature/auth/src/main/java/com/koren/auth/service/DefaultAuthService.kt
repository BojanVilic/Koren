package com.koren.auth.service

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import javax.inject.Inject

class DefaultAuthService @Inject constructor(
    private val credentialManager: CredentialManager,
    private val auth: FirebaseAuth,
    private val googleAuthService: GoogleAuthService
): AuthService {

    override suspend fun signIn(signInMethod: SignInMethod): Result<Unit> {
        return when (signInMethod) {
            SignInMethod.EMAIL -> Result.failure(Exception("Email sign in is not supported yet!"))
            SignInMethod.GOOGLE -> googleAuthService()
        }
    }
    override suspend fun signOut(): Result<Unit> {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            auth.signOut()
            return Result.success(Unit)
        } catch(e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}