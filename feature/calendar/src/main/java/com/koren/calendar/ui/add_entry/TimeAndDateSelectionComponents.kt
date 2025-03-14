package com.koren.calendar.ui.add_entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koren.common.models.invitation.toHumanReadableDate
import com.koren.designsystem.icon.Clock
import com.koren.designsystem.icon.KorenIcons

enum class PickerType {
    NONE, START_DATE, END_DATE, START_TIME, END_TIME
}

@Composable
fun TimeSelection(
    date: String,
    time: String,
    onTimeChanged: (String) -> Unit
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
                text = time.ifEmpty { "Time" },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    when (currentPicker) {
        PickerType.START_TIME -> TimePickerModal(
            initialHour = time.substringBefore(":").toIntOrNull() ?: 0,
            initialMinute = time.substringAfter(":").toIntOrNull() ?: 0,
            onTimeSelected = { selectedTime ->
                onTimeChanged(selectedTime ?: "")
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.NONE -> Unit
        else -> Unit
    }
}

@Composable
fun DateSelection(
    startDate: Long,
    endDate: Long,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit
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
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentPicker = PickerType.START_DATE
                    },
                text = if (startDate == 0L) "Start date" else startDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentPicker = PickerType.END_DATE
                    },
                text = if (endDate == 0L) "End date" else endDate.toHumanReadableDate(),
                style = MaterialTheme.typography.bodyLarge
            )
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
        PickerType.NONE -> Unit
        else -> Unit
    }
}

@Composable
fun TimeAndDateSelection(
    startDate: Long,
    endDate: Long,
    startTime: String,
    endTime: String,
    isAllDay: Boolean,
    isAllDayChanged: (Boolean) -> Unit,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit,
    onStartTimeChanged: (String) -> Unit,
    onEndTimeChanged: (String) -> Unit
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
                        text = startTime.ifEmpty { "Start time" },
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
                        text = endTime.ifEmpty { "End time" },
                        style = MaterialTheme.typography.bodyLarge
                    )
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
            initialHour = startTime.substringBefore(":").toIntOrNull() ?: 0,
            initialMinute = startTime.substringAfter(":").toIntOrNull() ?: 0,
            onTimeSelected = { selectedTime ->
                if ((selectedTime ?: "") > endTime) {
                    onEndTimeChanged(selectedTime ?: "")
                }
                onStartTimeChanged(selectedTime ?: "")
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.END_TIME -> TimePickerModal(
            initialHour = endTime.substringBefore(":").toIntOrNull() ?: 0,
            initialMinute = endTime.substringAfter(":").toIntOrNull() ?: 0,
            onTimeSelected = { selectedTime ->
                if ((selectedTime ?: "") < startTime) {
                    onStartTimeChanged(selectedTime ?: "")
                }
                onEndTimeChanged(selectedTime ?: "")
                currentPicker = PickerType.NONE
            },
            onDismiss = { currentPicker = PickerType.NONE }
        )
        PickerType.NONE -> Unit
    }
}