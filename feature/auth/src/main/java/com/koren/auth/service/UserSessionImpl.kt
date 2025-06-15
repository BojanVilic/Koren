package com.koren.auth.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.values
import com.google.firebase.messaging.FirebaseMessaging
import com.koren.common.models.user.UserData
import com.koren.common.services.UserNotLoggedInException
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class UserSessionImpl : UserSession {

    init {
        Firebase.database.setPersistenceEnabled(false)
    }

    private val auth = Firebase.auth

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<UserData> = flow {
        if (auth.currentUser?.uid == null) throw UserNotLoggedInException("User is not logged in")

        val userData = Firebase.database.getReference("users")
            .child(auth.currentUser?.uid ?: "")
            .values<UserData>()
            .filterNotNull()

        emitAll(userData)

    }.flowOn(Dispatchers.Default)

    override suspend fun updateUserDataOnSignIn() {
        val currentUserData = Firebase.database.getReference("users")
            .child(auth.currentUser?.uid ?: "")
            .get()
            .await()
            .getValue<UserData>()

        val fcmToken = FirebaseMessaging.getInstance().token.await()

        Firebase.database.getReference("users")
            .child(auth.currentUser?.uid ?: "")
            .setValue(
                currentUserData?.copy(
                    id = auth.currentUser?.uid ?: currentUserData.id,
                    email = auth.currentUser?.email ?: currentUserData.email,
                    displayName = auth.currentUser?.displayName ?: currentUserData.displayName,
                    fcmToken = fcmToken
                )?: UserData(
                    id = auth.currentUser?.uid ?: "",
                    email = auth.currentUser?.email ?: "",
                    displayName = auth.currentUser?.displayName ?: "",
                    fcmToken = fcmToken
                )
            )
    }
}