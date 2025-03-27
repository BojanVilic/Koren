package com.koren.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFamilyMemberUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    operator fun invoke(userId: String): Flow<UserData> = callbackFlow {
        val query = firebaseDatabase.getReference("users/$userId")

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<UserData>()?.let { user ->
                    trySend(user).isSuccess
                }?: close()
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.Default)
}