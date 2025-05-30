package com.koren.auth.service

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import javax.inject.Inject

class DefaultAuthService @Inject constructor(
    private val credentialManager: CredentialManager,
    private val auth: FirebaseAuth,
    private val googleAuthService: GoogleAuthService,
    private val emailAuthService: EmailAuthService,
    private val userSession: UserSession,
    private val firebaseDatabase: FirebaseDatabase
): AuthService {

    override suspend fun signIn(signInMethod: SignInMethod): Result<Unit> {
        return when (signInMethod) {
            is SignInMethod.Email -> emailAuthService.signIn(signInMethod.email, signInMethod.password)
            is SignInMethod.Google -> googleAuthService()
        }
            .onSuccess {
                userSession.updateUserDataOnSignIn()
            }
    }

    override suspend fun signUp(email: String, password: String, result: (Result<Unit>) -> Unit) {
        emailAuthService.signUp(email, password, result)
    }

    override suspend fun signOut(): Result<Unit> {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            auth.signOut()
            firebaseDatabase.purgeOutstandingWrites()
            return Result.success(Unit)
        } catch(e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }
}