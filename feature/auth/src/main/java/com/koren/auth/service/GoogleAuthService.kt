package com.koren.auth.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.koren.auth.R
import com.koren.common.services.ResourceProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleAuthService @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val credentialManager: CredentialManager,
    private val context: Context,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(): Result<Unit> {
        val googleSignRequest: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(getSignInWithGoogleOption())
            .build()

        try {
            val result = credentialManager.getCredential(
                request = googleSignRequest,
                context = context,
            )

            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    auth.signInWithCredential(googleCredentials).await()
                    return Result.success(Unit)
                } catch (e: GoogleIdTokenParsingException) {
                    return Result.failure(e)
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.failure(NoCredentialException("No credential found"))
    }

    private fun getSignInWithGoogleOption(): GetSignInWithGoogleOption {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it ->
            str + "%02x".format(it)
        }

        return GetSignInWithGoogleOption.Builder(resourceProvider[R.string.server_client_id])
            .setNonce(hashedNonce)
            .build()
    }
}