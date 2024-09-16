package com.koren.auth.service

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.koren.common.models.UserInfo
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserSessionImpl : UserSession {

    private val auth = Firebase.auth

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<UserInfo?>
        get() = callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(
                    UserInfo(
                        id = firebaseAuth.currentUser?.uid ?: "",
                        displayName = firebaseAuth.currentUser?.displayName ?: "",
                        email = firebaseAuth.currentUser?.email ?: ""
                    )
                )
            }
            auth.addAuthStateListener(authStateListener)
            awaitClose { auth.removeAuthStateListener(authStateListener) }
        }

}