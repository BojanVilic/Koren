package com.koren.account.ui.edit_profile

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.domain.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val userSession: UserSession
): StateViewModel<EditProfileUiEvent, EditProfileUiState, EditProfileSideEffect>() {

    override fun setInitialState(): EditProfileUiState = EditProfileUiState.Loading

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            userSession.currentUser.collect { user ->
                _uiState.update {
                    EditProfileUiState.Shown(
                        userData = user,
                        eventSink = ::handleEvent
                    )
                }
            }
        }
    }

    override fun handleEvent(event: EditProfileUiEvent) {
        withEventfulState<EditProfileUiState.Shown> { currentState ->
            when (event) {
                is EditProfileUiEvent.UploadNewProfilePicture -> _uiState.update { currentState.copy(newProfilePicture = event.uri) }
                is EditProfileUiEvent.OnNameChange -> _uiState.update { currentState.copy(userData = currentState.userData?.copy(displayName = event.name)) }
                is EditProfileUiEvent.SaveProfile -> saveProfile(currentState)
            }
        }
    }

    private fun saveProfile(currentState: EditProfileUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            currentState.userData?.let {
                updateUserProfileUseCase.invoke(currentState.userData, currentState.newProfilePicture)
                    .onSuccess { _sideEffects.emitSuspended(EditProfileSideEffect.ShowSnackbar("Profile saved successfully!")) }
                    .onFailure { _sideEffects.emitSuspended(EditProfileSideEffect.ShowSnackbar(it.message.orUnknownError())) }
            }
        }
    }
}