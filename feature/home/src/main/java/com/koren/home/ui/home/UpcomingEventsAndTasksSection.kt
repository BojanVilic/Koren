package com.koren.home.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.models.user.UserData
import com.koren.common.util.DateUtils.toHumanReadableDateTime
import com.koren.common.util.DateUtils.toHumanReadableDateTimeRange
import com.koren.designsystem.icon.Circle
import com.koren.designsystem.icon.CircleCheck
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Warning
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Calendar

fun LazyListScope.upcomingEventsAndTasks(
    uiState: HomeUiState.Shown
) {
    if (uiState.events.isNotEmpty()) {
        item {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                text = "Upcoming Events",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(uiState.events) { event ->
            Spacer(modifier = Modifier.height(4.dp))
            EventItem(event = event)
        }
    }

    if (uiState.tasks.isNotEmpty()) {
        item {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 16.dp),
                text = "Upcoming Tasks",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(uiState.tasks) { task ->
            Spacer(modifier = Modifier.height(4.dp))
            TaskItem(task = task)
        }
    }

    if (uiState.tasks.isEmpty() && uiState.events.isEmpty()) {
        item {
            FreeDay(uiState)
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Event Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val timeRange = Pair(event.eventStartTime, event.eventEndTime)
                Text(
                    text = if (event.allDay) "All Day" else timeRange.toHumanReadableDateTimeRange(atLocalTimeZone = true),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    if (task.taskTimestamp < Calendar.getInstance().timeInMillis) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier
                    .offset(y = (12.dp)),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 16.dp, top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = KorenIcons.Warning,
                        contentDescription = "Warning Icon",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Overdue",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (task.completed) KorenIcons.CircleCheck else KorenIcons.Circle
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = "Task Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Due: ${task.taskTimestamp.toHumanReadableDateTime(atLocalTimeZone = true)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FreeDay(
    uiState: HomeUiState.Shown
) {
    val freeDayEmojis = remember {
        listOf(
            "\u2600\uFE0F", // Sun
            "\uD83D\uDD4A\uFE0F", // Dove of Peace
            "\u2615", // Hot Beverage
            "\uD83C\uDFD6\uFE0F", // Beach with Umbrella
            "\uD83C\uDF34", // Palm Tree
            "\uD83E\uDE81", // Kite
            "\u26F0\uFE0F", // Mountain
            "\uD83C\uDFDE\uFE0F" // National Park
        )
    }
    val randomEmoji = remember { freeDayEmojis.random() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Hello, ${uiState.currentUser.displayName}!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = randomEmoji,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Looks like you have a free day today.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        when (uiState.freeDayNextItem) {
            is NextItem.TaskItem -> {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                UpcomingItemTask(uiState.freeDayNextItem)
            }
            is NextItem.EventItem -> {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                UpcomingItemEvent(uiState.freeDayNextItem)
            }
            is NextItem.None -> Unit
        }
    }
}

@Composable
private fun UpcomingItemEvent(
    eventItem: NextItem.EventItem
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Next up:",
        style = MaterialTheme.typography.bodyMedium
    )
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Outlined.DateRange,
            contentDescription = "Event Icon",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = eventItem.event.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "due/at ${eventItem.event.eventStartTime.toHumanReadableDateTime()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun UpcomingItemTask(
    taskItem: NextItem.TaskItem
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Next up:",
        style = MaterialTheme.typography.bodyMedium
    )
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (taskItem.task.completed) KorenIcons.CircleCheck else KorenIcons.Circle
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = "Task Icon",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = taskItem.task.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Due: ${taskItem.task.taskTimestamp.toHumanReadableDateTime()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "From: ${taskItem.task.creator?.displayName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@ThemePreview
@Composable
fun FreeDayWithNextItem() {
    KorenTheme {
        Surface {
            FreeDay(
                uiState = HomeUiState.Shown(
                    currentUser = UserData(
                        displayName = "John Doe",
                    ),
                    freeDayNextItem = NextItem.TaskItem(
                        TaskWithUsers(
                            title = "Grocery Shopping",
                            taskTimestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                        )
                    ),
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
fun FreeDayWithoutNextItem() {
    KorenTheme {
        Surface {
            FreeDay(
                uiState = HomeUiState.Shown(
                    currentUser = UserData(
                        displayName = "John Doe",
                    ),
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
fun UpcomingEventsAndTasksPreview() {
    val tasks = listOf(
        Task(title = "Grocery Shopping", taskTimestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis),
        Task(title = "Pay Bills", taskTimestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.timeInMillis),
        Task(title = "Water the Plants", taskTimestamp = Calendar.getInstance().timeInMillis)
    )
    val events = listOf(
        Event(title = "Family Dinner", eventStartTime = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 19); set(
            Calendar.MINUTE, 0) }.timeInMillis, eventEndTime = Calendar.getInstance().apply { set(
            Calendar.HOUR_OF_DAY, 21); set(Calendar.MINUTE, 0) }.timeInMillis),
        Event(title = "Doctor Appointment", eventStartTime = Calendar.getInstance().apply { add(
            Calendar.DAY_OF_YEAR, 2); set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 30) }.timeInMillis, eventEndTime = Calendar.getInstance().apply { add(
            Calendar.DAY_OF_YEAR, 2); set(Calendar.HOUR_OF_DAY, 11); set(Calendar.MINUTE, 0) }.timeInMillis),
        Event(title = "Weekend Getaway", eventStartTime = Calendar.getInstance().apply { add(
            Calendar.DAY_OF_YEAR, 5) }.timeInMillis, eventEndTime = Calendar.getInstance().apply { add(
            Calendar.DAY_OF_YEAR, 7) }.timeInMillis, allDay = true)
    )
    KorenTheme {
        LazyColumn {
            upcomingEventsAndTasks(
                HomeUiState.Shown(
                    currentUser = UserData(
                        displayName = "John Doe",
                    ),
                    tasks = tasks,
                    events = events,
                    eventSink = {}
                )
            )
        }
    }
}