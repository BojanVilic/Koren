package com.koren.home.ui.create_family

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.home.usecases.CreateFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFamilyViewModel @Inject constructor(
    private val createFamilyUseCase: CreateFamilyUseCase
): ViewModel() {

    private val _state = MutableStateFlow(CreateFamilyState())
    val state: StateFlow<CreateFamilyState> = _state.asStateFlow()

    fun setPhotoUri(uri: Uri?) {
        _state.update { currentState ->
            currentState.copy(photoUri = uri)
        }
    }

    fun setFamilyName(name: String) {
        _state.update { currentState ->
            currentState.copy(familyName = name)
        }
    }

    fun nextStep() {
        val current = _state.value.currentStep
        if (current < _state.value.totalSteps - 1) {
            _state.update { currentState ->
                currentState.copy(currentStep = current + 1)
            }
        }
    }

    fun previousStep() {
        val current = _state.value.currentStep
        if (current > 0) {
            _state.update { currentState -> currentState.copy(currentStep = current - 1) }
        }
    }

    fun createFamily() {
        viewModelScope.launch {
            createFamilyUseCase(_state.value.familyName, _state.value.photoUri).collect { status ->
                _state.update { currentState ->
                    currentState.copy(familyCreationStatus = status)
                }
            }
        }
    }
}