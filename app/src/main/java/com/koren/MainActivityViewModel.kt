package com.koren

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.models.UserData
import com.koren.common.services.UserNotLoggedInException
import com.koren.common.services.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!userSession.isLoggedIn) _uiState.value = MainActivityUiState.LoggedOut
            else {
                userSession.currentUser.collect {
                    try {
                        _uiState.value = MainActivityUiState.Success(it)
                    }
                    catch (e: UserNotLoggedInException) {
                        _uiState.value = MainActivityUiState.LoggedOut
                    }
                    catch (e: Exception) {
                        _uiState.value = MainActivityUiState.Error(e.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
    data class Error(val message: String) : MainActivityUiState
    data object LoggedOut : MainActivityUiState

    fun shouldKeepSplashScreen() = this is Loading
}