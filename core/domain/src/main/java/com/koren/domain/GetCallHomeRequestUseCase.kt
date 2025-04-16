package com.koren.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.family.CallHomeRequest
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.models.family.CallHomeRequestWithUser
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetCallHomeRequestUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    operator fun invoke(): Flow<CallHomeRequestWithUser?> = callbackFlow {
        val user = userSession.currentUser.first()
        val query = firebaseDatabase.getReference("families/${user.familyId}/callHomeRequests/${user.id}")

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val callHomeRequest = snapshot.getValue<CallHomeRequest>()

                    callHomeRequest?.let {
                        val requester = firebaseDatabase.reference
                            .child("users/${callHomeRequest.requesterId}")
                            .get()
                            .await()
                            .getValue<UserData>()

                        val callHomeRequestWithUser = CallHomeRequestWithUser(
                            requester = requester ?: UserData(),
                            timestamp = callHomeRequest.timestamp,
                            status = callHomeRequest.status
                        )
                        trySend(callHomeRequestWithUser).isSuccess
                    }?: run {
                        trySend(null).isSuccess
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null).isSuccess
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.Default)
}