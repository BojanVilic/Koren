package com.koren.chat.ui.attachments_list

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.koren.chat.ui.full_screen_image.FullScreenImageUiEvent
import com.koren.chat.ui.full_screen_image.FullScreenImageUiSideEffect
import com.koren.chat.ui.full_screen_image.FullScreenImageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.koren.common.util.MoleculeViewModel
import com.koren.common.util.orUnknownError
import com.koren.domain.GetAttachmentForMessageUseCase

@HiltViewModel
class AttachmentsListViewModel @Inject constructor(
    private val getAttachmentForMessageUseCase: GetAttachmentForMessageUseCase,
    private val savedStateHandle: SavedStateHandle
) : MoleculeViewModel<AttachmentsListUiEvent, AttachmentsListUiState, AttachmentsListUiSideEffect>() {

    override fun setInitialState(): AttachmentsListUiState = AttachmentsListUiState.Loading

    @Composable
    override fun produceState(): AttachmentsListUiState {
        val messageId = savedStateHandle.toRoute<AttachmentsListDestination>().messageId
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