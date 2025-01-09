package com.koren.onboarding.ui.create_family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.domain.GetFamilyUseCase
import com.koren.onboarding.usecases.CreateFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFamilyViewModel @Inject constructor(
    private val createFamilyUseCase: CreateFamilyUseCase,
    private val getFamilyUseCase: GetFamilyUseCase
): ViewModel() {

    private val _state = MutableStateFlow<CreateFamilyUiState>(CreateFamilyUiState.Loading)
    val state: StateFlow<CreateFamilyUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFamilyUseCase()
                .onSuccess { family -> _state.update { CreateFamilyUiState.Error(errorMessage = "You are already a member of the ${family.name} family.\n\nTo create a new family, please leave your current family from the account section in the app.") } }
                .onFailure { _state.update { CreateFamilyUiState.Step(eventSink = ::handleEvent) } }
        }
    }

    private fun handleEvent(event: CreateFamilyEvent) {
        withStepState { currentState ->
            when (event) {
                is CreateFamilyEvent.SetFamilyName -> _state.update { currentState.copy(familyName = event.name) }
                is CreateFamilyEvent.SetPhotoUri -> _state.update { currentState.copy(photoUri = event.uri) }
                is CreateFamilyEvent.NextStep -> nextStep(currentState)
                is CreateFamilyEvent.PreviousStep -> previousStep(currentState)
                is CreateFamilyEvent.CreateFamily -> createFamily(currentState)
            }
        }
    }

    private fun nextStep(currentState: CreateFamilyUiState.Step) {
        if (currentState.currentStep < currentState.totalSteps - 1) {
            _state.update {
                currentState.copy(currentStep = currentState.currentStep + 1)
            }
        }
    }

    private fun previousStep(currentState: CreateFamilyUiState.Step) {
        if (currentState.currentStep > 0) {
            _state.update {
                currentState.copy(currentStep = currentState.currentStep - 1)
            }
        }
    }

    private fun createFamily(currentState: CreateFamilyUiState.Step) {
        viewModelScope.launch {
            _state.update { CreateFamilyUiState.CreatingFamily }
            try {
                createFamilyUseCase(currentState.familyName, currentState.photoUri)
                _state.update { CreateFamilyUiState.FamilyCreated }
            } catch (e: Exception) {
                _state.update { CreateFamilyUiState.Error(e.message?: "Unknown error.") }
            }
        }
    }

    private inline fun withStepState(action: (CreateFamilyUiState.Step) -> Unit) {
        val currentState = _state.value
        if (currentState is CreateFamilyUiState.Step) {
            action(currentState)
        }
    }
}