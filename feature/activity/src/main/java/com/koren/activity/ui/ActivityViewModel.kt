package com.koren.activity.ui

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.repository.ActivityRepository
import com.koren.domain.GetFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val userSession: UserSession
): StateViewModel<ActivityEvent, ActivityUiState, ActivitySideEffect>() {

    override fun setInitialState(): ActivityUiState = ActivityUiState.Loading

    init {
        combine(
            activityRepository.getLocationActivities(),
            userSession.currentUser
        ) { activities, user ->
            _uiState.update {
                if (user.familyId.isEmpty()) ActivityUiState.NoFamily
                else ActivityUiState.Shown(
                    activities = activities,
                    eventSink = ::handleEvent
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: ActivityEvent) {
        withEventfulState<ActivityUiState.Shown> {
            when (event) {
                ActivityEvent.OnClick -> Unit
            }
        }
    }
}