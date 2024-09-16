package com.koren.home.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.home.usecases.CreateFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createFamilyUseCase: CreateFamilyUseCase,
    private val userSession: UserSession
): ViewModel() {

    val currentUser = userSession.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    suspend fun createFamily(familyName: String, familyPortraitPath: Uri? = null) {
        createFamilyUseCase(familyName, familyPortraitPath)
    }
}