@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.add_entry

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    TimeSelectionRow(uiState = uiState)
    HorizontalDivider()
    DescriptionRow(
        description = uiState.description,
        onDescriptionChanged = { uiState.eventSink(AddEntryUiEvent.DescriptionChanged(it)) }
    )
    HorizontalDivider()
}
enum class PickerType {
    NONE, START_DATE, END_DATE, START_TIME, END_TIME
}

@Composable
private fun TimeSelectionRow(
    uiState: AddEntryUiState.Shown.AddEvent
) {

    var currentPicker by remember { mutableStateOf(PickerType.NONE) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = KorenIcons.Clock,
                contentDescription = "Event icon"
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "All day",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.isAllDay,
                onCheckedChange = { uiState.eventSink(AddEntryUiEvent.IsAllDayChanged(it)) }
            )
        }

        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(32.dp))
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentPicker = PickerType.START_DATE
                    },
                text = if (uiState.startDate == 0L) "Start date" else uiState.startDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
            if (!uiState.isAllDay) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(32.dp))
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                currentPicker = PickerType.START_TIME
                            },
                        text = uiState.startTime.ifEmpty { "Start time" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(32.dp))
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentPicker = PickerType.END_DATE
                    },
                text = if (uiState.endDate == 0L) "End date" else uiState.endDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!uiState.isAllDay) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(32.dp))
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                currentPicker = PickerType.END_TIME
                            },
                        text = uiState.endTime.ifEmpty { "End time" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    when (currentPicker) {
        PickerType.START_DATE -> DatePickerModal(
            onDateSelected = { selectedDate ->
                if ((selectedDate?: 0) > uiState.endDate) {
                    uiState.eventSink(AddEntryUiEvent.EndDateChanged(selectedDate ?: 0L))
                }
                uiState.eventSink(AddEntryUiEvent.StartDateChanged(selectedDate ?: 0L))
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.END_DATE -> DatePickerModal(
            onDateSelected = { selectedDate ->
                if ((selectedDate?: 0) < uiState.startDate) {
                    uiState.eventSink(AddEntryUiEvent.StartDateChanged(selectedDate ?: 0L))
                }
                uiState.eventSink(AddEntryUiEvent.EndDateChanged(selectedDate ?: 0L))
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.START_TIME -> TimePickerModal(
            initialHour = uiState.startTime.substringBefore(":").toIntOrNull()?: 0,
            initialMinute = uiState.startTime.substringAfter(":").toIntOrNull()?: 0,
            onTimeSelected = { selectedTime ->
                if ((selectedTime ?: "") > uiState.endTime) {
                    uiState.eventSink(AddEntryUiEvent.EndTimeChanged(selectedTime ?: ""))
                }
                uiState.eventSink(AddEntryUiEvent.StartTimeChanged(selectedTime ?: ""))
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.END_TIME -> TimePickerModal(
            initialHour = uiState.endTime.substringBefore(":").toIntOrNull()?: 0,
            initialMinute = uiState.endTime.substringAfter(":").toIntOrNull()?: 0,
            onTimeSelected = { selectedTime ->
                if ((selectedTime ?: "") < uiState.startTime) {
                    uiState.eventSink(AddEntryUiEvent.StartTimeChanged(selectedTime ?: ""))
                }
                uiState.eventSink(AddEntryUiEvent.EndTimeChanged(selectedTime ?: ""))
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.NONE -> Unit
    }
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

@SuppressLint("DefaultLocale")
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
    Text(text = "Add task content")
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