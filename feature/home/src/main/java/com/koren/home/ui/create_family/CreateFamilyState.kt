package com.koren.home.ui.create_family

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

data class CreateFamilyState(
    val photoUri: Uri? = null,
    val familyName: String = "",
    val currentStep: Int = 0
) {
    val isStepValid: Boolean by derivedStateOf {
        when (currentStep) {
            0 -> photoUri != null
            1 -> familyName.isNotBlank()
            2 -> true
            else -> false
        }
    }

    val totalSteps: Int = 3
}