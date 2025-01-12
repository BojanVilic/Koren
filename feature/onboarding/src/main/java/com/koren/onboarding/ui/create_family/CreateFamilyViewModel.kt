package com.koren.onboarding.ui.create_family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.util.StateViewModel
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
): StateViewModel<CreateFamilyEvent, CreateFamilyUiState, Nothing>() {

    override fun setInitialState(): CreateFamilyUiState = CreateFamilyUiState.Loading

    init {
        viewModelScope.launch {
            getFamilyUseCase()
                .onSuccess { family -> _uiState.update { CreateFamilyUiState.Error(errorMessage = "You are already a member of the ${family.name} family.\n\nTo create a new family, please leave your current family from the account section in the app.") } }
                .onFailure { _uiState.update { CreateFamilyUiState.Step(eventSink = ::handleEvent) } }
        }
    }

    override fun handleEvent(event: CreateFamilyEvent) {
        withEventfulState<CreateFamilyUiState.Step> { currentState ->
            when (event) {
                is CreateFamilyEvent.SetFamilyName -> _uiState.update { currentState.copy(familyName = event.name) }
                is CreateFamilyEvent.SetPhotoUri -> _uiState.update { currentState.copy(photoUri = event.uri) }
                is CreateFamilyEvent.NextStep -> nextStep(currentState)
                is CreateFamilyEvent.PreviousStep -> previousStep(currentState)
                is CreateFamilyEvent.CreateFamily -> createFamily(currentState)
            }
        }
    }

    private fun nextStep(currentState: CreateFamilyUiState.Step) {
        if (currentState.currentStep < currentState.totalSteps - 1) {
            _uiState.update {
                currentState.copy(currentStep = currentState.currentStep + 1)
            }
        }
    }

    private fun previousStep(currentState: CreateFamilyUiState.Step) {
        if (currentState.currentStep > 0) {
            _uiState.update {
                currentState.copy(currentStep = currentState.currentStep - 1)
            }
        }
    }

    private fun createFamily(currentState: CreateFamilyUiState.Step) {
        viewModelScope.launch {
            _uiState.update { CreateFamilyUiState.CreatingFamily }
            try {
                createFamilyUseCase(currentState.familyName, currentState.photoUri)
                _uiState.update { CreateFamilyUiState.FamilyCreated }
            } catch (e: Exception) {
                _uiState.update { CreateFamilyUiState.Error(e.message?: "Unknown error.") }
            }
        }
    }
}