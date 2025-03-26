package com.koren.home.ui.home.member_details

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.domain.GetFamilyLocations
import com.koren.domain.GetFamilyMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MemberDetailsViewModel @Inject constructor(
    private val getFamilyMemberUseCase: GetFamilyMemberUseCase,
    private val userSession: UserSession
): StateViewModel<MemberDetailsUiEvent, MemberDetailsUiState, MemberDetailsUiSideEffect>() {

    override fun setInitialState(): MemberDetailsUiState = MemberDetailsUiState.Loading

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    fun init(userId: String) {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            val currentUser by userSession.currentUser.collectAsState(initial = UserData())

            if (currentUser.id == userId) {
                _uiState.update {
                    MemberDetailsUiState.SelfDetails
                }
                return@launchMolecule
            }

            val familyMemberDetails by getFamilyMemberUseCase(userId).collectAsState(initial = UserData())
            val currentUserLat = currentUser.lastLocation?.latitude?: 0.0
            val currentUserLon = currentUser.lastLocation?.longitude?: 0.0
            val memberLat = familyMemberDetails.lastLocation?.latitude?: 0.0
            val memberLon = familyMemberDetails.lastLocation?.longitude?: 0.0

            val distance = SphericalUtil.computeDistanceBetween(LatLng(currentUserLat, currentUserLon), LatLng(memberLat, memberLon)).toLong()
            var distanceString = "${distance}m away"

            if (distance > 2000) {
                val distanceKm = distance / 1000.0
                distanceString = String.format(Locale.getDefault(),"%.1fkm away", distanceKm)
            }

            _uiState.update {
                MemberDetailsUiState.Shown(
                    member = familyMemberDetails,
                    distanceText = distanceString,
                    eventSink = { event -> handleEvent(event) }
                )
            }
        }
    }

    override fun handleEvent(event: MemberDetailsUiEvent) {
        withEventfulState<MemberDetailsUiState.Shown> { currentState ->
            when (event) {
                else -> {}
            }
        }
    }
}