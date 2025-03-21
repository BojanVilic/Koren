package com.koren.calendar.ui.day_details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.calendar.Day
import com.koren.calendar.ui.add_entry.AddEntryScreen
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toHumanReadableDateTimeRange
import com.koren.common.util.DateUtils.toTime
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.icon.Circle
import com.koren.designsystem.icon.CircleCheck
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.ExtendedTheme
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.designsystem.theme.ThemePreview
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DayDetailsScreen(
    viewModel: DayDetailsViewModel = hiltViewModel(),
    day: Day,
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
            is DayDetailsUiSideEffect.ShowSnackbar -> snackbarHost.showSnackbar(uiSideEffect.message)
        }
    }

    DayDetailsScreenContent(
        uiState = uiState,
        onDismiss = onDismiss
    )
}

@Composable
private fun DayDetailsScreenContent(
    uiState: DayDetailsUiState,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                stiffness = Spring.StiffnessHigh
            )
        )
    ) {
        when (uiState) {
            is DayDetailsUiState.Loading -> CircularProgressIndicator()
            is DayDetailsUiState.Shown.Idle -> DayDetailsScreenShownContent(uiState = uiState)
            is DayDetailsUiState.Shown.Empty -> EmptyDayDetailsContent(uiState = uiState)
            is DayDetailsUiState.Shown.AddEntry -> AddEntryScreen(
                day = uiState.day,
                onDismiss = { onDismiss() }
            )
        }
    }
}

@Composable
private fun EmptyDayDetailsContent(
    uiState: DayDetailsUiState.Shown.Empty
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = uiState.day.localDate?.format(DateTimeFormatter.ofPattern("EEEE, dd MMM"))?: "",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Your day is wide open!\nAdd events and tasks to make the most of it.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = { uiState.eventSink(DayDetailsUiEvent.AddClicked) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
                Text(text = "Add")
            }
        }
    }
}

@Composable
private fun DayDetailsScreenShownContent(
    uiState: DayDetailsUiState.Shown.Idle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.day.localDate?.format(DateTimeFormatter.ofPattern("EEEE, dd MMM"))?: "",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            FloatingActionButton(
                onClick = { uiState.eventSink(DayDetailsUiEvent.AddClicked) },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Task or Event")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.tasks.isNotEmpty()) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.tasks) { task ->
                    TaskItem(task = task)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.events.isNotEmpty()) {
            Text(
                text = "Events",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.events) { event ->
                    EventItem(event = event)
                }
            }
        }

        if (uiState.tasks.isEmpty() && uiState.events.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "No tasks or events for this day.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun TaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = if (task.completed) KorenIcons.CircleCheck else KorenIcons.Circle,
            contentDescription = if (task.completed) "Task Completed" else "Task Pending",
            tint = if (task.completed) ExtendedTheme.colors.task else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (task.taskTimestamp > 0) {
            val time = task.taskTimestamp.toTime(atLocalTimeZone = true)
            Text(
                text = time,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EventItem(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ExtendedTheme.colors.event)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!event.allDay) {
                val timeRange = Pair(event.eventStartTime, event.eventEndTime).toHumanReadableDateTimeRange()
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = "All Day",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@ThemePreview
@Composable
fun DayDetailsScreenPreview() {
    KorenTheme {
        DayDetailsScreenContent(
            uiState = DayDetailsUiState.Shown.Idle(
                day = Day(
                    dayOfMonth = 4,
                    dayOfWeek = DayOfWeek.MONDAY,
                    tasks = listOf(
                        Task(
                            title = "Task 1",
                            description = "Description 1",
                            taskTimestamp = 1640000000000,
                            completed = false,
                            creatorUserId = "1",
                            assigneeUserId = "2"
                        ),
                        Task(
                            title = "Task 2",
                            description = "Description 2",
                            taskTimestamp = 1640000000000,
                            completed = false,
                            creatorUserId = "1",
                            assigneeUserId = "2"
                        )
                    ),
                    events = listOf(
                        Event(
                            title = "Event 1",
                            description = "Description 1",
                            eventStartTime = 1640000000000,
                            eventEndTime = 1640000000000,
                            creatorUserId = "1",
                            allDay = false
                        ),
                        Event(
                            title = "Event 2",
                            description = "Description 2",
                            eventStartTime = 1640000000000,
                            eventEndTime = 1640000000000,
                            creatorUserId = "1",
                            allDay = true
                        )
                    )
                ),
                eventSink = {}
            ),
            onDismiss = {}
        )
    }
}