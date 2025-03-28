package com.koren.home.ui.home.member_details

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.domain.GetFamilyMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MemberDetailsViewModel @Inject constructor(
    private val getFamilyMemberUseCase: GetFamilyMemberUseCase,
    private val userSession: UserSession
): StateViewModel<MemberDetailsUiEvent, MemberDetailsUiState, MemberDetailsUiSideEffect>() {

    override fun setInitialState(): MemberDetailsUiState = MemberDetailsUiState.Loading

    fun init(userId: String) {
        viewModelScope.launch {
            combine(
                userSession.currentUser,
                getFamilyMemberUseCase(userId)
            ) { currentUser, familyMemberDetails ->
                currentUser to familyMemberDetails
            }
            .collect { (currentUser, familyMemberDetails) ->

                Timber.d("Current user id: ${currentUser.id}, family member id: $userId")
                if (currentUser.id == userId) {
                    _uiState.update {
                        MemberDetailsUiState.SelfDetails
                    }
                    return@collect
                }
                val distanceString = getDistanceBetweenUsers(currentUser, familyMemberDetails)

                _uiState.getAndUpdate {
                    when (it) {
                        is MemberDetailsUiState.Shown -> it.copy(
                            member = familyMemberDetails,
                            distanceText = distanceString,
                            eventSink = { event -> handleEvent(event) }
                        )
                        else -> MemberDetailsUiState.Shown(
                            member = familyMemberDetails,
                            distanceText = distanceString,
                            eventSink = { event -> handleEvent(event) }
                        )
                    }
                }
            }
        }
    }

    private fun getDistanceBetweenUsers(
        currentUser: UserData,
        familyMemberDetails: UserData
    ): String {
        val currentUserLat = currentUser.lastLocation?.latitude ?: 0.0
        val currentUserLon = currentUser.lastLocation?.longitude ?: 0.0
        val memberLat = familyMemberDetails.lastLocation?.latitude ?: 0.0
        val memberLon = familyMemberDetails.lastLocation?.longitude ?: 0.0

        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(currentUserLat, currentUserLon),
            LatLng(memberLat, memberLon)
        ).toLong()
        var distanceString = "${distance}m away"

        if (distance > 2000) {
            val distanceKm = distance / 1000.0
            distanceString = String.format(Locale.getDefault(), "%.1fkm away", distanceKm)
        }
        return distanceString
    }

    override fun handleEvent(event: MemberDetailsUiEvent) {
        withEventfulState<MemberDetailsUiState.Shown> { currentState ->
            when (event) {
                is MemberDetailsUiEvent.CallHome -> Unit
                is MemberDetailsUiEvent.FindOnMap -> _sideEffects.emitSuspended(MemberDetailsUiSideEffect.NavigateAndFindOnMap(currentState.member.id))
                is MemberDetailsUiEvent.ViewAssignedTasks -> Unit
                is MemberDetailsUiEvent.EditRole -> Unit
            }
        }
    }
}