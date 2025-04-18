package com.koren.domain

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.maps.android.SphericalUtil
import com.koren.common.models.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetDistanceToHomeUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val getFamilyUseCase: GetFamilyUseCase,
    private val removeCallHomeRequestUseCase: RemoveCallHomeRequestUseCase
) {
    operator fun invoke(userId: String): Flow<Int> = callbackFlow {
        val family = getFamilyUseCase().getOrNull()

        val ref = firebaseDatabase.reference.child("users/$userId")
        val listener = ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    launch(Dispatchers.IO) {
                        val user = snapshot.getValue<UserData>()

                        val currentUserLat = user?.lastLocation?.latitude ?: 0.0
                        val currentUserLon = user?.lastLocation?.longitude ?: 0.0
                        val homeLat = family?.homeLat ?: 0.0
                        val homeLng = family?.homeLng ?: 0.0

                        val distance = SphericalUtil.computeDistanceBetween(
                            LatLng(currentUserLat, currentUserLon),
                            LatLng(homeLat, homeLng)
                        ).toLong().toInt()

                        trySend(distance).isSuccess

                        if (distance < 100) {
                            removeCallHomeRequestUseCase()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { ref.removeEventListener(listener) }
    }
}