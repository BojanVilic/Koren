package com.koren.chat.ui.attachments_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.koren.common.util.MoleculeViewModel
import com.koren.common.util.orUnknownError
import com.koren.domain.GetAttachmentForMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttachmentsListViewModel @Inject constructor(
    private val getAttachmentForMessageUseCase: GetAttachmentForMessageUseCase,
    savedStateHandle: SavedStateHandle
) : MoleculeViewModel<AttachmentsListUiEvent, AttachmentsListUiState, AttachmentsListUiSideEffect>() {

    private val messageId = savedStateHandle.toRoute<AttachmentsListDestination>().messageId

    override fun setInitialState(): AttachmentsListUiState = AttachmentsListUiState.Loading

    @Composable
    override fun produceState(): AttachmentsListUiState {
        var mediaUrls by remember { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(Unit) {
            getAttachmentForMessageUseCase(messageId)
                .onSuccess { mediaUrls = it }
                .onFailure { _sideEffects.emitSuspended(AttachmentsListUiSideEffect.ShowError(it.message.orUnknownError())) }
        }

        if (mediaUrls.size == 1) _sideEffects.emitSuspended(AttachmentsListUiSideEffect.NavigateToFullScreenImage(mediaUrls.first()))

        return AttachmentsListUiState.Shown(
            mediaUrls = mediaUrls
        ) { event ->
            when (event) {
                is AttachmentsListUiEvent.OnImageClick -> _sideEffects.emitSuspended(AttachmentsListUiSideEffect.NavigateToFullScreenImage(event.imageUrl))
            }
        }
    }
}