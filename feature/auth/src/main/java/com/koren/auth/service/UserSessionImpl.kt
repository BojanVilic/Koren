package com.koren.auth.service

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.koren.common.models.UserData
import com.koren.common.services.UserNotLoggedInException
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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
            var userData = UserData()
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                userData = UserData(
                    id = auth.currentUser?.uid ?: "",
                    displayName = firebaseAuth.currentUser?.displayName ?: "",
                    email = firebaseAuth.currentUser?.email ?: ""
                )
            }
            auth.addAuthStateListener(authStateListener)

            val familyRef = Firebase.database.getReference("users")
                .child(auth.currentUser?.uid ?: "")
                .child("familyId")

            try {
                val snapshot = familyRef.get().await()
                userData = userData.copy(hasFamily = snapshot.exists())
                trySend(userData)
            } catch (e: Exception) {
                trySend(userData)
            }

            awaitClose { auth.removeAuthStateListener(authStateListener) }
        }
}