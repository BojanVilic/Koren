package com.koren.designsystem.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.koren.designsystem.theme.LocalSnackbarHostState

@Composable
fun SimpleSnackbar(
    message: String,
    snackbarType: SnackbarType = SnackbarType.Message,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    if (message.isEmpty()) return

    when (snackbarType) {
        is SnackbarType.Message -> Message(message, duration)
        is SnackbarType.MessageWithDismiss -> MessageWithDismiss(message, duration)
        is SnackbarType.MessageWithCustomAction -> MessageWithCustomAction(message, duration, snackbarType.actionLabel, snackbarType.onActionPerformed)
    }
}

@Composable
private fun Message(
    message: String,
    duration: SnackbarDuration
) {
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(message) {
        snackbarHostState
            .showSnackbar(
                message = message,
                duration = duration
            )
    }
}

@Composable
private fun MessageWithDismiss(
    message: String,
    duration: SnackbarDuration
) {
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(message) {
        snackbarHostState
            .showSnackbar(
                message = message,
                duration = duration,
                withDismissAction = true
            )
    }
}

@Composable
private fun MessageWithCustomAction(
    message: String,
    duration: SnackbarDuration,
    actionLabel: String,
    onActionPerformed: () -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(message) {
        val result = snackbarHostState
            .showSnackbar(
                message = message,
                duration = duration,
                actionLabel = actionLabel
            )

        if (result == SnackbarResult.ActionPerformed) onActionPerformed()
    }
}

sealed class SnackbarType {
    data object Message : SnackbarType()
    data object MessageWithDismiss : SnackbarType()
    class MessageWithCustomAction(val actionLabel: String, val onActionPerformed: () -> Unit) : SnackbarType()
}