package com.koren.home.ui.create_family

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.koren.home.usecases.CreateFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateFamilyViewModel @Inject constructor(
    private val createFamilyUseCase: CreateFamilyUseCase
): ViewModel() {

    private val _state = MutableStateFlow(CreateFamilyState())
    val state: StateFlow<CreateFamilyState> = _state.asStateFlow()

    fun setPhotoUri(uri: Uri?) {
        _state.value = _state.value.copy(photoUri = uri)
    }

    fun setFamilyName(name: String) {
        _state.value = _state.value.copy(familyName = name)
    }

    fun nextStep() {
        val current = _state.value.currentStep
        if (current < _state.value.totalSteps - 1) {
            _state.value = _state.value.copy(currentStep = current + 1)
        }
    }

    fun previousStep() {
        val current = _state.value.currentStep
        if (current > 0) {
            _state.value = _state.value.copy(currentStep = current - 1)
        }
    }

    suspend fun createFamily() {
        createFamilyUseCase(_state.value.familyName, _state.value.photoUri)
    }
}