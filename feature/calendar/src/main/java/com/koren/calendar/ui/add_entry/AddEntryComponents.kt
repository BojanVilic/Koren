@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.add_entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.TabItem
import com.koren.designsystem.components.Tabs
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Task
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.designsystem.theme.ThemePreview

@Composable
fun AddEntryScreen(
    viewModel: AddEntryViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    val snackbarHost = LocalSnackbarHostState.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is AddEntryUiSideEffect.ShowSnackbar -> snackbarHost.showSnackbar(uiSideEffect.message)
            is AddEntryUiSideEffect.Dismiss -> onDismiss()
        }
    }

    AddEntryScreenContent(
        uiState = uiState
    )
}

@Composable
private fun AddEntryScreenContent(
    uiState: AddEntryUiState
) {
    when (uiState) {
        is AddEntryUiState.Loading -> CircularProgressIndicator()
        is AddEntryUiState.Shown -> AddEntryScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun AddEntryScreenShownContent(
    uiState: AddEntryUiState.Shown
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TextButton(
                onClick = { uiState.eventSink(AddEntryUiEvent.CancelClicked) }
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                value = uiState.title,
                onValueChange = { uiState.eventSink(AddEntryUiEvent.TitleChanged(it)) },
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Add title",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BottomSheetDefaults.ContainerColor,
                    unfocusedBorderColor = BottomSheetDefaults.ContainerColor
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                singleLine = true
            )

            val tabIndex = remember { mutableIntStateOf(0) }

            Tabs(
                modifier = Modifier.fillMaxWidth(),
                tabIndex = tabIndex,
                onTabChanged = { uiState.eventSink(AddEntryUiEvent.TabChanged(it)) },
                items = listOf(
                    TabItem(
                        title = "Event",
                        icon = KorenIcons.Event
                    ),
                    TabItem(
                        title = "Task",
                        icon = KorenIcons.Task
                    )
                )
            )

            when (uiState) {
                is AddEntryUiState.Shown.AddEvent -> AddEventContent(uiState)
                is AddEntryUiState.Shown.AddTask -> AddTaskContent(uiState)
            }
        }
    }
}

@Composable
private fun AddEventContent(
    uiState: AddEntryUiState.Shown.AddEvent
) {
    Text(text = "Add event content")
}

@Composable
private fun AddTaskContent(
    uiState: AddEntryUiState.Shown.AddTask
) {
    Text(text = "Add task content")
}

@ThemePreview
@Composable
fun AddEntryScreenPreview() {
    KorenTheme {
        AddEntryScreenContent(
            uiState = AddEntryUiState.Shown.AddEvent(
                eventSink = {}
            )
        )
    }
}