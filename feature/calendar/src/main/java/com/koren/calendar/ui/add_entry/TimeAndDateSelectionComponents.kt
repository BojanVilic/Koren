@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.add_entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koren.common.util.DateUtils.toHumanReadableDate
import com.koren.common.util.HourMinute
import com.koren.common.util.isAfter
import com.koren.common.util.isBefore
import com.koren.designsystem.icon.Clock
import com.koren.designsystem.icon.KorenIcons

enum class PickerType {
    NONE, START_DATE, END_DATE, START_TIME, END_TIME
}

@Composable
fun TimeSelection(
    date: String,
    time: HourMinute?,
    timeError: Boolean,
    onTimeChanged: (HourMinute) -> Unit
) {
    var currentPicker by remember { mutableStateOf(PickerType.NONE) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(32.dp))
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = date,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentPicker = PickerType.START_TIME
                    },
                text = time?.toString()?: "Select time",
                style = MaterialTheme.typography.bodyLarge
            )

            AnimatedVisibility(timeError) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            currentPicker = PickerType.START_TIME
                        },
                    text = "Time is required",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    when (currentPicker) {
        PickerType.START_TIME -> TimePickerModal(
            initialHour = time?.hour ?: 0,
            initialMinute = time?.minute ?: 0,
            onTimeSelected = { selectedTime ->
                onTimeChanged(selectedTime)
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.NONE -> Unit
        else -> Unit
    }
}

@Composable
fun TimeAndDateSelection(
    startDate: Long,
    endDate: Long,
    startTime: HourMinute?,
    endTime: HourMinute?,
    isAllDay: Boolean,
    startTimeError: Boolean,
    endTimeError: Boolean,
    isAllDayChanged: (Boolean) -> Unit,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit,
    onStartTimeChanged: (HourMinute) -> Unit,
    onEndTimeChanged: (HourMinute) -> Unit
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
                checked = isAllDay,
                onCheckedChange = { isAllDayChanged(it) }
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
                text = if (startDate == 0L) "Start date" else startDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
            if (!isAllDay) {
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
                        text = startTime?.toString() ?: "Start time",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    AnimatedVisibility(startTimeError) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    currentPicker = PickerType.START_TIME
                                },
                            text = "Time is required",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
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
                text = if (endDate == 0L) "End date" else endDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!isAllDay) {
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
                        text = endTime?.toString()?: "End time",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    AnimatedVisibility(endTimeError) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    currentPicker = PickerType.END_TIME
                                },
                            text = "Time is required",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    when (currentPicker) {
        PickerType.START_DATE -> DatePickerModal(
            onDateSelected = { selectedDate ->
                if ((selectedDate ?: 0) > endDate) {
                    onEndDateChanged(selectedDate ?: 0L)
                }
                onStartDateChanged(selectedDate ?: 0L)
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.END_DATE -> DatePickerModal(
            onDateSelected = { selectedDate ->
                if ((selectedDate ?: 0) < startDate) {
                    onStartDateChanged(selectedDate ?: 0L)
                }
                onEndDateChanged(selectedDate ?: 0L)
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.START_TIME -> TimePickerModal(
            initialHour = startTime?.hour?: 0,
            initialMinute = startTime?.minute ?: 0,
            onTimeSelected = { selectedTime ->
                if (selectedTime.isAfter(endTime?: HourMinute(0, 0))) {
                    onEndTimeChanged(selectedTime)
                }
                onStartTimeChanged(selectedTime)
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.END_TIME -> TimePickerModal(
            initialHour = endTime?.hour ?: 0,
            initialMinute = endTime?.minute ?: 0,
            onTimeSelected = { selectedTime ->
                if (selectedTime.isBefore(startTime?: HourMinute(0, 0))) {
                    onStartTimeChanged(selectedTime)
                }
                onEndTimeChanged(selectedTime)
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.NONE -> Unit
    }
}

@Composable
fun TimePickerModal(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimeSelected: (HourMinute) -> Unit,
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
                    onTimeSelected(HourMinute(timePickerState.hour, timePickerState.minute))
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