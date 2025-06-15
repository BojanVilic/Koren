package com.koren

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.Constants
import com.koren.common.util.MoleculeViewModel
import com.koren.map.service.LocationUpdateScheduler
import com.koren.navigation.MainActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userSession: UserSession,
    private val locationUpdateScheduler: LocationUpdateScheduler
) : MoleculeViewModel<Nothing, MainActivityUiState, Nothing>() {

    override fun setInitialState(): MainActivityUiState = MainActivityUiState.Loading

    @Composable
    override fun produceState(): MainActivityUiState {
        if (!userSession.isLoggedIn) return MainActivityUiState.LoggedOut
        val userData by userSession.currentUser.collectAsState(initial = null)
        if (userData == null) return MainActivityUiState.Loading

        LaunchedEffect(userData?.id) {
            val frequency = userData?.locationUpdateFrequencyInMins ?: Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS
            locationUpdateScheduler.schedulePeriodicUpdates(frequency.toLong())
        }

        return MainActivityUiState.Success(userData = userData?: UserData())
    }
}