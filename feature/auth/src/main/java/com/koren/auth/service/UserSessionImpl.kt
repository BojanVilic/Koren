package com.koren.auth.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.koren.common.models.UserData
import com.koren.common.services.UserNotLoggedInException
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class UserSessionImpl : UserSession {

    init {
        Firebase.database.setPersistenceEnabled(true)
    }

    private val auth = Firebase.auth

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<UserData>
        get() = callbackFlow {
            if (auth.currentUser?.uid == null) throw UserNotLoggedInException("User is not logged in")

            Firebase.database.getReference("users")
                .child(auth.currentUser?.uid ?: "")
                .get()
                .addOnSuccessListener {
                    trySend(it.getValue<UserData>()?: throw IllegalStateException("User data is null"))
                }
                .addOnFailureListener {
                    Timber.e("Failed to get user data")
                }

            awaitClose()
        }

    override suspend fun updateUserDataOnLogin() {
        val currentUserData = Firebase.database.getReference("users")
            .child(auth.currentUser?.uid ?: "")
            .get()
            .await()
            .getValue<UserData>()

        Firebase.database.getReference("users")
            .child(auth.currentUser?.uid ?: "")
            .setValue(
                currentUserData?.copy(
                    id = auth.currentUser?.uid ?: "",
                    email = auth.currentUser?.email ?: "",
                    displayName = auth.currentUser?.displayName ?: ""
                )?: UserData(
                    id = auth.currentUser?.uid ?: "",
                    email = auth.currentUser?.email ?: "",
                    displayName = auth.currentUser?.displayName ?: ""
                )
            )
    }
}