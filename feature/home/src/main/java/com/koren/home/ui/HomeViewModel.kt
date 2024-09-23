package com.koren.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userSession: UserSession
): ViewModel() {

    val currentUser = userSession.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}