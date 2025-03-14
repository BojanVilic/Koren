@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.add_entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.calendar.ui.Day
import com.koren.common.models.invitation.toHumanReadableDate
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.TabItem
import com.koren.designsystem.components.Tabs
import com.koren.designsystem.icon.Clock
import com.koren.designsystem.icon.Content
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Task
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.designsystem.theme.ThemePreview
import java.time.ZoneOffset

@Composable
fun AddEntryScreen(
    day: Day,
    viewModel: AddEntryViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    DisposableEffectWithLifecycle(
        onCreate = { viewModel.init(day = day) }
    )

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
        }

        when (uiState) {
            is AddEntryUiState.Shown.AddEvent -> AddEventContent(uiState)
            is AddEntryUiState.Shown.AddTask -> AddTaskContent(uiState)
        }
    }
}

@Composable
private fun AddEventContent(
    uiState: AddEntryUiState.Shown.AddEvent
) {
    TimeAndDateSelection(
        startDate = uiState.startDate,
        endDate = uiState.endDate,
        startTime = uiState.startTime,
        endTime = uiState.endTime,
        isAllDay = uiState.isAllDay,
        isAllDayChanged = { uiState.eventSink(AddEntryUiEvent.IsAllDayChanged(it)) },
        onStartDateChanged = { uiState.eventSink(AddEntryUiEvent.StartDateChanged(it)) },
        onEndDateChanged = { uiState.eventSink(AddEntryUiEvent.EndDateChanged(it)) },
        onStartTimeChanged = { uiState.eventSink(AddEntryUiEvent.StartTimeChanged(it)) },
        onEndTimeChanged = { uiState.eventSink(AddEntryUiEvent.EndTimeChanged(it)) }
    )
    HorizontalDivider()
    DescriptionRow(
        description = uiState.description,
        onDescriptionChanged = { uiState.eventSink(AddEntryUiEvent.DescriptionChanged(it)) }
    )
    HorizontalDivider()
}

@Composable
private fun DescriptionRow(
    description: String,
    onDescriptionChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = KorenIcons.Content,
            contentDescription = "Event icon"
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { onDescriptionChanged(it) },
            placeholder = {
                Text(
                    text = "Add description",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BottomSheetDefaults.ContainerColor,
                unfocusedBorderColor = BottomSheetDefaults.ContainerColor
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TimePickerModal(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimeSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedHour = timePickerState.hour
                    val selectedMinute = timePickerState.minute
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    onTimeSelected(formattedTime)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        }
    )
}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun AddTaskContent(
    uiState: AddEntryUiState.Shown.AddTask
) {
    TimeSelection(
        date = (uiState.selectedDay.localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L).toHumanReadableDate(),
        time = uiState.time,
        onTimeChanged = { uiState.eventSink(AddEntryUiEvent.StartTimeChanged(it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    DescriptionRow(
        description = uiState.description,
        onDescriptionChanged = { uiState.eventSink(AddEntryUiEvent.DescriptionChanged(it)) }
    )
    HorizontalDivider()
}

@ThemePreview
@Composable
fun AddEventPreview() {
    KorenTheme {
        AddEntryScreenContent(
            uiState = AddEntryUiState.Shown.AddEvent(
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
fun AddTaskPreview() {
    KorenTheme {
        AddEntryScreenContent(
            uiState = AddEntryUiState.Shown.AddTask(
                eventSink = {}
            )
        )
    }
}