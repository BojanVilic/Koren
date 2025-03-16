@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.add_entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.toDayDateMonth
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.TabItem
import com.koren.designsystem.components.Tabs
import com.koren.designsystem.icon.Content
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.RemovePerson
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
                onClick = {
                    uiState.eventSink(AddEntryUiEvent.SaveClicked)
                }
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                isError = uiState.titleError
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
        startTimeError = uiState.startTimeError,
        endTimeError = uiState.endTimeError,
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
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            )
        )
    }
}

@Composable
private fun AddTaskContent(
    uiState: AddEntryUiState.Shown.AddTask
) {
    TimeSelection(
        date = uiState.selectedDay.toDayDateMonth(),
        time = uiState.time,
        timeError = uiState.timeError,
        onTimeChanged = { uiState.eventSink(AddEntryUiEvent.StartTimeChanged(it)) }
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    DescriptionRow(
        description = uiState.description,
        onDescriptionChanged = { uiState.eventSink(AddEntryUiEvent.DescriptionChanged(it)) }
    )
    HorizontalDivider()
    AssigneeSelection(uiState = uiState)
}


@Composable
fun AssigneeSelection(uiState: AddEntryUiState.Shown.AddTask) {

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .data(uiState.selectedAssignee?.profilePictureUrl)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = uiState.assigneeDropdownExpanded,
            onExpandedChange = {
                uiState.eventSink(AddEntryUiEvent.AssigneeDropdownExpandedChanged(it))
            }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = uiState.assigneeSearchQuery,
                onValueChange = { uiState.eventSink(AddEntryUiEvent.AssigneeSearchQueryChanged(it)) },
                label = { Text("Assignee") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = uiState.assigneeDropdownExpanded
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BottomSheetDefaults.ContainerColor,
                    unfocusedBorderColor = BottomSheetDefaults.ContainerColor
                ),
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words
                ),
                isError = uiState.assigneeError
            )
            ExposedDropdownMenu(
                expanded = uiState.assigneeDropdownExpanded,
                onDismissRequest = {
                    uiState.eventSink(AddEntryUiEvent.AssigneeDropdownExpandedChanged(false))
                }
            ) {
                uiState.filteredAssignees.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            uiState.eventSink(AddEntryUiEvent.AssigneeSelected(selectionOption))
                        },
                        text = { Text(text = selectionOption.displayName) },
                        leadingIcon = {
                            AsyncImage(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .crossfade(true)
                                    .data(selectionOption.profilePictureUrl)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        },
                        trailingIcon = if (selectionOption.id == uiState.selectedAssignee?.id) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        } else null
                    )
                }
            }
        }

        AnimatedVisibility(uiState.selectedAssignee != null) {
            TextButton(
                onClick = {
                    uiState.eventSink(AddEntryUiEvent.RemoveSelectedAssignee)
                }
            ) {
                Icon(
                    imageVector = KorenIcons.RemovePerson,
                    contentDescription = "Remove assignee"
                )
            }
        }
    }
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